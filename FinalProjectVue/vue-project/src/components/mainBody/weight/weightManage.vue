<template>
  <div class="container">
    <div class="chart-container">
      <canvas ref="weightChart"></canvas>
    </div>
    <div class="input-panel">
      <div class="weight-input">
        <h3>체중 입력</h3>
        <div class="input-group">
          <div class="input-field">
            <label>날짜</label>
            <div class="date-input-container">
              <button class="date-arrow" @click="changeDate(-1)">←</button>
              <input 
                type="date" 
                v-model="newEntry.date"
                :max="getCurrentDate()"
              >
              <button class="date-arrow" @click="changeDate(1)">→</button>
            </div>
          </div>
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
        <button class="add-btn" @click="addWeight">추가 / 변경</button>
        <button class="delete-btn" @click="deleteWeight">삭제</button>
      </div>
      
      <div class="trend-info">
        <div class="trend-item">
          <span>체중 변화: {{ calculateWeightTrend() }}kg/주</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Chart from 'chart.js/auto';
import axios from 'axios';

const weightChart = ref(null);
let chart = null;
const weightData = ref([]);
const userNo = ref('1');

const newEntry = ref({
  date: getCurrentDate(),
  weight: null
});

function getCurrentDate() {
  return new Date().toISOString().split('T')[0];
}

function changeDate(days) {
  const currentDate = new Date(newEntry.value.date);
  currentDate.setDate(currentDate.getDate() + days);
  
  const today = new Date();
  if (currentDate > today) {
    return;
  }
  
  newEntry.value.date = currentDate.toISOString().split('T')[0];
}

function calculateWeightTrend() {
  const filteredData = weightData.value.filter(d => d.weight !== null);
  
  if (filteredData.length < 2) return '0.0';
  
  const sortedData = [...filteredData]
    .sort((a, b) => new Date(a.date) - new Date(b.date));
  
  const recentData = sortedData.slice(-28);
  
  if (recentData.length < 2) return '0.0';
  
  const firstWeight = parseFloat(recentData[0].weight);
  const lastWeight = parseFloat(recentData[recentData.length - 1].weight);
  const weeksPassed = (new Date(recentData[recentData.length - 1].date) - new Date(recentData[0].date)) / (1000 * 60 * 60 * 24 * 7);
  
  const weeklyChange = (lastWeight - firstWeight) / weeksPassed;
  return weeklyChange.toFixed(1);
}

function calculateTrendLine(data) {
  // null이 아닌 데이터만 필터링
  const validData = data.filter(d => d.weight !== null);
  
  if (validData.length < 2) return [];
  
  const xValues = validData.map((_, i) => i);
  const yValues = validData.map(d => parseFloat(d.weight));
  
  const n = validData.length;
  const sumX = xValues.reduce((a, b) => a + b, 0);
  const sumY = yValues.reduce((a, b) => a + b, 0);
  const sumXY = xValues.reduce((a, b, i) => a + b * yValues[i], 0);
  const sumXX = xValues.reduce((a, b) => a + b * b, 0);
  
  const slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
  const intercept = (sumY - slope * sumX) / n;
  
  return xValues.map(x => slope * x + intercept);
}

function initChart() {
  if (chart) {
    chart.destroy();
  }

  if (!weightChart.value) return;

  // weight가 null이 아닌 데이터만 필터링 후 정렬
  const filteredData = weightData.value
    .filter(d => d.weight !== null && d.weight !== 0)  // null과 0 모두 제외
    .sort((a, b) => new Date(a.date) - new Date(b.date));

  if (filteredData.length === 0) {
    // 유효한 데이터가 없을 경우 빈 차트 표시
    const ctx = weightChart.value.getContext('2d');
    chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: [],
        datasets: [{
          label: '체중',
          data: [],
          borderColor: '#36A2EB',
          backgroundColor: '#36A2EB'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });
    return;
  }

  const ctx = weightChart.value.getContext('2d');
  const trendLineData = calculateTrendLine(filteredData);
  
  chart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: filteredData.map(d => d.date),
      datasets: [
        {
          label: '체중',
          data: filteredData.map(d => ({
            x: d.date,
            y: parseFloat(d.weight)
          })),
          borderColor: '#36A2EB',
          backgroundColor: '#36A2EB',
          tension: 0.4,
          fill: false,
          spanGaps: true  // null 값을 건너뛰도록 설정
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
      scales: {
        y: {
          beginAtZero: false,
          suggestedMin: filteredData.length > 0 ? 
            Math.min(...filteredData.map(d => parseFloat(d.weight))) - 1 : 0,
          suggestedMax: filteredData.length > 0 ? 
            Math.max(...filteredData.map(d => parseFloat(d.weight))) + 1 : 100
        }
      },
      plugins: {
        tooltip: {
          mode: 'index',
          intersect: false
        }
      }
    }
  });
}

async function loadWeightData() {
  try {
    const response = await axios.get(`http://localhost:8080/calendar/weight-data/${userNo.value}`);
    weightData.value = response.data;
    initChart();
  } catch(error) {
    console.error('데이터 로딩 실패:', error);
  }
}

async function addWeight() {
  if (!newEntry.value.date || !newEntry.value.weight) return;
  
  try {
    const existingEntry = weightData.value.find(d => d.date === newEntry.value.date);

    if (existingEntry) {
      const response = await axios.put(
        `http://localhost:8080/calendar/weight-update/${existingEntry.calendarNo}`,
        newEntry.value
      );
      const index = weightData.value.findIndex(d => d.date === newEntry.value.date);
      weightData.value[index] = response.data;
    } else {
      const response = await axios.post(
        `http://localhost:8080/calendar/weight-add/${userNo.value}`,
        newEntry.value
      );
      weightData.value.push(response.data);
    }
    
    if (chart) {
      chart.destroy();
    }
    initChart();
    
    newEntry.value = {
      date: getCurrentDate(),
      weight: null
    };
    
  } catch(error) {
    console.error('데이터 추가 실패:', error);
  }
}
async function deleteWeight() {
  if (!newEntry.value.date) return;
  
  try {
    const existingEntry = weightData.value.find(d => d.date === newEntry.value.date);
    
    if (existingEntry) {
      // weight만 null로 업데이트
      const updateData = {
        ...existingEntry,
        weight: null
      };
      
      const response = await axios.put(
        `http://localhost:8080/calendar/weight-update/${existingEntry.calendarNo}`,
        updateData
      );
      
      // 로컬 데이터 업데이트
      const index = weightData.value.findIndex(d => d.date === newEntry.value.date);
      weightData.value[index] = response.data;
      
      // 차트 업데이트
      if (chart) {
        chart.destroy();
      }
      initChart();
      
      // 입력 폼 초기화
      newEntry.value = {
        date: getCurrentDate(),
        weight: null
      };
    } else {
      alert('선택한 날짜의 데이터가 없습니다.');
    }
  } catch(error) {
    console.error('데이터 삭제 실패:', error);
  }
}


onMounted(() => {
  loadWeightData();
});
</script>





<style scoped>
.container {
  display: flex;
  gap: 20px;
  padding: 20px;
  height: 600px;
}

.chart-container {
  flex: 2;
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.input-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.weight-input {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 15px;
}

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
.date-input-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

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

</style>