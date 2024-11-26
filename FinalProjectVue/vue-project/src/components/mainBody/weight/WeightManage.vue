<template>
  <!-- 전체 컨테이너 -->
  <div class="container">
    <!-- 체중 그래프가 표시될 영역 -->
    <div class="chart-container">
      <canvas ref="weightChart"></canvas>
    </div>

    <!-- 입력 패널 영역 -->
    <div class="input-panel">
      <!-- 체중 입력 폼 -->
      <div class="weight-input">
        <h3>체중 입력</h3>
        <div class="input-group">
          <!-- 날짜 입력 필드 -->
          <div class="input-field">
            <label>날짜</label>
            <div class="date-input-container">
              <!-- 이전 날짜 버튼 -->
              <button class="nav-btn" @click="changeDate(-1)">◀</button>
              <!-- 날짜 선택 입력 -->
              <input
                  type="date"
                  v-model="newEntry.date"
                  :max="getCurrentDate()"
              >
              <!-- 다음 날짜 버튼 -->
              <button class="nav-btn" @click="changeDate(1)">▶</button>
            </div>
          </div>

          <!-- 체중 입력 필드 -->
          <div class="input-field">
            <label>몸무게 (kg)</label>
            <input
                type="number"
                v-model="newEntry.weight"
                step="0.1"
                min="20"
                max="200"
            >
          </div>
        </div>

        <!-- 데이터 조작 버튼들 -->
        <button class="add-btn" @click="addWeight">추가 / 변경</button>
        <button class="delete-btn" @click="deleteWeight">삭제</button>
      </div>

      <!-- 체중 변화 트렌드 정보 표시 영역 -->
      <div class="trend-info">
        <div class="trend-item">
          <h3>체중 분석</h3>
          <div class="trend-details">
            <span>월간 체중 변화량: {{ weightTrends.monthlyChange }}kg</span>
            <span>주간 체중 변화량: {{ weightTrends.weeklyChange }}kg</span>
            <span>평균 체중: {{ weightTrends.averageWeight }}kg</span>
            <span>기록 일관성: {{ weightTrends.consistency }}%</span>
            <span class="recommendation">{{ weightTrends.recommendation }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 필요한 Vue 컴포지션 API와 외부 라이브러리 임포트
import {ref, onMounted, watch} from 'vue';
import Chart from 'chart.js/auto';
import axiosInstance from "@/plugins/axios.js";
import 'chartjs-adapter-date-fns';
import { ko } from 'date-fns/locale';

// 차트 관련 참조 변수들
const weightChart = ref(null);
let chart = null;
const weightData = ref([]);

// 새로운 체중 기록을 위한 데이터 객체
const newEntry = ref({
  date: getCurrentDate(),
  weight: null
});

const weightTrends = ref({
  yearlyChange: '0.0',
  monthlyChange: '0.0',
  weeklyChange: '0.0',
  averageWeight: '0.0',
  consistency: '0.0',
  recommendation: ''
});

// 현재 날짜를 YYYY-MM-DD 형식으로 반환하는 함수
function getCurrentDate() {
  const now = new Date();
  now.setHours(now.getHours() + 9); // KST 보정
  return now.toISOString().split('T')[0];
}

function changeDate(days) {
  const parts = newEntry.value.date.split('-');
  const currentDate = new Date(Date.UTC(parts[0], parts[1] - 1, parts[2]));
  currentDate.setUTCDate(currentDate.getUTCDate() + days);

  const today = new Date();
  const utcToday = new Date(Date.UTC(today.getFullYear(), today.getMonth(), today.getDate()));

  if (currentDate > utcToday) return;

  newEntry.value = {
    ...newEntry.value,
    date: currentDate.toISOString().split('T')[0],
    weight: weightData.value.find(d => d.date === currentDate.toISOString().split('T')[0])?.weight || null,
  };
}

function calculateWeightTrends() {
  const currentDate = new Date();
  const oneMonthAgo = new Date(currentDate);
  oneMonthAgo.setMonth(currentDate.getMonth() - 1);

  const filteredData = weightData.value.filter(d => {
    const dataDate = new Date(d.date);
    return d.weight !== null && !isNaN(d.weight) && dataDate >= oneMonthAgo;
  });

  if (filteredData.length < 2) {
    return {
      monthlyChange: 0,
      weeklyChange: 0,
      averageWeight: 0,
      minWeight: 0,
      maxWeight: 0,
      dataPoints: 0,
      trend: 'insufficient_data',
      recommendation: '최소 2개 이상의 체중 기록이 필요합니다.'
    };
  }

  const sortedData = [...filteredData].sort((a, b) => new Date(a.date) - new Date(b.date));
  const weights = sortedData.map(d => parseFloat(d.weight));
  const firstWeight = weights[0];
  const lastWeight = weights[weights.length - 1];
  const avgWeight = weights.reduce((a, b) => a + b) / weights.length;
  const minWeight = Math.min(...weights);
  const maxWeight = Math.max(...weights);

  // 월간 체중 변화량 계산: (마지막 체중 - 첫 번째 체중)
  const totalChange = lastWeight - firstWeight;

  // 월간 체중 변화량
  const monthlyChange = totalChange;

  // 주간 체중 변화량 (월간 변화량을 4주로 나누기)
  const weeklyChange = (monthlyChange / 4.345).toFixed(1);  // 4주 기준으로 나누기

  let trend = 'stable';
  let recommendation = '';
  const changePercent = (totalChange / firstWeight) * 100;

  if (Math.abs(monthlyChange) < 0.5) {
    trend = 'stable';
    recommendation = '현재 체중이 안정적으로 유지되고 있습니다.';
  } else if (monthlyChange > 0) {
    trend = 'increasing';
    recommendation = monthlyChange > 2 ? '체중 증가 속도가 빠릅니다. 식단 조절과 운동을 고려하세요.' : '완만한 체중 증가 추세입니다.';
  } else {
    trend = 'decreasing';
    recommendation = monthlyChange < -2 ? '체중 감소 속도가 빠릅니다. 영양 섭취와 건강 상태를 확인해보세요.' : '건강한 체중 감소 추세입니다.';
  }

  return {
    monthlyChange: monthlyChange.toFixed(1),
    weeklyChange: weeklyChange,  // 주간 체중 변화량
    averageWeight: avgWeight.toFixed(1),
    minWeight: minWeight.toFixed(1),
    maxWeight: maxWeight.toFixed(1),
    dataPoints: weights.length,
    changePercent: changePercent.toFixed(1),
    trend,
    recommendation,
    consistency: (weights.length / 30 * 100).toFixed(1)
  };
}



// 추세선 계산 함수
function calculateTrendLine(data) {
  const validData = data.filter(d => d.weight !== null);
  if (validData.length < 2) return [];

  // 선형 회귀 계산을 위한 변수들
  const xValues = validData.map((_, i) => i);
  const yValues = validData.map(d => parseFloat(d.weight));
  const n = validData.length;
  const sumX = xValues.reduce((a, b) => a + b, 0);
  const sumY = yValues.reduce((a, b) => a + b, 0);
  const sumXY = xValues.reduce((a, b, i) => a + b * yValues[i], 0);
  const sumXX = xValues.reduce((a, b) => a + b * b, 0);

  // 선형 회귀 계수 계산
  const slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
  const intercept = (sumY - slope * sumX) / n;

  return xValues.map(x => slope * x + intercept);
}

// 차트 초기화 및 업데이트 함수
function initChart() {
  if (chart) {
    chart.destroy();
  }
  if (!weightChart.value) return;

  const dataArray = Array.isArray(weightData.value) ? weightData.value : [];
  const filteredData = dataArray
      .filter(d => d.weight !== null && d.weight !== 0)
      .sort((a, b) => new Date(a.date) - new Date(b.date));

  const ctx = weightChart.value.getContext('2d');
  const trendLineData = calculateTrendLine(filteredData);

  chart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: filteredData.map(d => d.date),
      datasets: [
        {
          label: '체중',
          data: filteredData.map(d => ({ x: d.date, y: parseFloat(d.weight) })),
          borderColor: '#36A2EB',
          backgroundColor: '#36A2EB',
          tension: 0.4,
          fill: false,
          spanGaps: true
        },
        {
          label: '추세선',
          data: trendLineData.length > 0 ? trendLineData : [],
          borderColor: '#FF6384',
          borderDash: [5, 5],
          tension: 0.4,
          fill: false
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: '체중 변화 추이',
          font: {
            size: 20,
            weight: 'bold'
          },
          padding: {
            top: 10,
            bottom: 30
          }
        },
        tooltip: {
          mode: 'index',
          intersect: false
        }
      },
      scales: {
        x: {
          type: 'category',
          adapters: {
            date: { locale: ko }
          },
          min: filteredData[0]?.date,
          max: filteredData[filteredData.length - 1]?.date
        },
        y: {
          beginAtZero: false,
          suggestedMin: filteredData.length > 0 ? Math.min(...filteredData.map(d => parseFloat(d.weight))) - 1 : 0,
          suggestedMax: filteredData.length > 0 ? Math.max(...filteredData.map(d => parseFloat(d.weight))) + 1 : 100
        }
      }
    }
  });
}

// 체중 데이터 로드 함수
async function loadWeightData() {
  try {
    const response = await axiosInstance.get(`/calendar/weight-data`);
    // 날짜 형식 변환
    weightData.value = Array.isArray(response.data) ? response.data.map(item => {
      const utcDate = new Date(item.date);
      const kstDate = new Date(utcDate.getTime() + 9 * 60 * 60 * 1000);
      return {
        ...item,
        date: kstDate.toISOString().split('T')[0]
      };
    }) : [];
    initChart();
    weightTrends.value = calculateWeightTrends();
  } catch(error) {
    console.error('데이터 로딩 실패:', error);
    weightData.value = [];
  }
}


// 체중 데이터 추가/수정 함수
async function addWeight() {
  if (!newEntry.value.date || !newEntry.value.weight) return;

  try {
    const existingEntry = weightData.value.find(d => d.date === newEntry.value.date);

    if (existingEntry) {
      // 기존 데이터 업데이트
      await axiosInstance.put(`/calendar/weight-update`, newEntry.value);
    } else {
      // 새 데이터 추가
      await axiosInstance.post(`/calendar/weight-add`, newEntry.value);
    }
    await loadWeightData();
  } catch(error) {
    console.error('데이터 추가 실패:', error);
  }
}

// 체중 데이터 삭제 함수
async function deleteWeight() {
  if (!newEntry.value.date || !newEntry.value.weight) return;

  try {
    const existingEntry = weightData.value.find(d => d.date === newEntry.value.date);

    if (existingEntry) {
      // URL 경로에 date를 포함시켜 DELETE 요청을 보냄
      await axiosInstance.delete(`/calendar/weight-delete/${newEntry.value.date}`);

      await loadWeightData();

      const today = getCurrentDate();
      const todayEntry = weightData.value.find(entry => entry.date === today);

      newEntry.value = {
        date: today,
        weight: todayEntry ? todayEntry.weight : null, // 현재 날짜 데이터가 있으면 몸무게 설정, 없으면 null
      };
    } else {
      alert('선택한 날짜의 데이터가 없습니다.');
    }
  } catch (error) {
    console.error('데이터 삭제 실패:', error);
  }
}


// 컴포넌트 마운트 시 데이터 로드
onMounted(async () => {
  await loadWeightData();

  // 현재 날짜와 일치하는 몸무게 값 설정
  const today = getCurrentDate();
  const todayEntry = weightData.value.find(entry => entry.date === today);

  newEntry.value = {
    date: today,
    weight: todayEntry ? todayEntry.weight : null, // 현재 날짜 데이터가 있으면 몸무게 설정, 없으면 null
  };
});

watch(() => newEntry.value.date, (newDate) => {
  const selectedEntry = weightData.value.find(entry => entry.date === newDate);

  newEntry.value = {
    date: newDate,
    weight: selectedEntry?.weight ?? null
  };
});
</script>

<style scoped>
/* 컨테이너 스타일링 */
.container {
  display: flex;
  gap: 20px;
  padding: 20px;
  height: 600px;
}

/* 차트 컨테이너 스타일링 */
.chart-container {
  flex: 2;
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* 입력 패널 스타일링 */
.input-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 체중 입력 폼 스타일링 */
.weight-input {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* 입력 그룹 스타일링 */
.input-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 15px;
}

/* 입력 필드 스타일링 */
.input-field {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.input-field label {
  font-size: 14px;
  color: #666;
}

.input-field input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

/* 버튼 스타일링 */
.add-btn {
  width: 100%;
  padding: 10px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.add-btn:hover {
  background: #45a049;
}

/* 트렌드 정보 스타일링 */
.trend-info {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.trend-item {
  text-align: center;
  font-size: 16px;
  color: #333;
}

/* 날짜 입력 컨테이너 스타일링 */
.date-input-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 날짜 이동 버튼 스타일링 */
.date-arrow {
  padding: 8px 12px;
  background: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.date-arrow:hover {
  background: #e0e0e0;
}
.delete-btn {
  width: 100%;
  padding: 10px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
  transition: background-color 0.2s;
}

.delete-btn:hover {
  background: #c82333;
}

.trend-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 10px;
}

.trend-details span {
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 5px;
  font-size: 14px;
}

.recommendation {
  color: #2196F3;
  font-weight: bold;
  background-color: #e3f2fd !important;
}

.trend-item h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
}
.nav-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  font-size: 16px;
}
</style>