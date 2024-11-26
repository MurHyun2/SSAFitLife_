<template>
  <div class="diet-plan">
    <!-- 달력 섹션 -->
    <div class="calendar-section">
      <div class="date-header">
        <div class="calendar-nav">
          <button @click="prevDay">〈</button>
          <span>{{ currentDate }} {{ getDayOfWeek() }}</span>
          <button @click="nextDay">〉</button>
        </div>
        <div class="calendar-wrapper">
          <VueDatePicker
              v-model="selectedDate"
              :format="format"
              :enable-time-picker="false"
              auto-apply
              locale="ko"
              :week-start="1"
          />
        </div>
      </div>
    </div>

    <!-- 칼로리 요약 섹션 -->
    <div class="calorie-summary">
      <div class="total-calories">
        <h2>칼로리</h2>
        <span class="big-number">{{ Math.round(totalFilteredCalories) }} / {{ targetCalories }}</span> Kcal<br>
        <span class="remaining">{{ Math.round(totalFilteredCalories - targetCalories) }} Kcal {{ totalFilteredCalories > targetCalories ? '초과' : '부족' }}</span>
      </div>
    </div>

    <!-- 식사 타입 버튼 -->
    <div class="meal-type-buttons">
      <button
          v-for="type in mealTypes"
          :key="type.id"
          :class="['meal-button', type.class, { active: selectedMealType === type.id }]"
          @click="selectMealType(type.id)"
      >
        {{ type.name }}
      </button>
      <button class="save-button" @click="saveDietList">저장하기</button>
    </div>

    <!-- 식사 기록 테이블 -->
    <div class="meal-table">
      <table>
        <thead>
        <tr>
          <th>식사구분</th>
          <th>음식명</th>
          <th>중량(g)</th>
          <th>열량(kcal)</th>
          <th>탄(g)</th>
          <th>단(g)</th>
          <th>지(g)</th>
          <th>삭제</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="diet in filteredMeals" :key="diet.dietNo" :data-meal-type="diet.dietType">
          <td>{{
              diet.dietType === 0 ? '아침' :
                  diet.dietType === 1 ? '점심' :
                      diet.dietType === 2 ? '저녁' :
                          diet.dietType === 3 ? '간식' : ''
            }}
          </td>
          <td>{{ diet.food.foodName }}</td>
          <td>{{ (diet.foodQuantity * diet.food.foodAmt).toFixed(1) }}g</td>
          <td>{{ (diet.foodQuantity * diet.food.foodCalorie).toFixed(1) }}kcal</td>
          <td>{{ (diet.foodQuantity * diet.food.foodCarb).toFixed(1) }}g</td>
          <td>{{ (diet.foodQuantity * diet.food.foodProtein).toFixed(1) }}g</td>
          <td>{{ (diet.foodQuantity * diet.food.foodFat).toFixed(1) }}g</td>
          <td>
            <button class="delete-btn" @click="deleteMeal(diet.dietNo)">❌</button>
          </td>
        </tr>
        </tbody>
      </table>
      <div class="table-footer">
        <div class="footer-buttons">
          <button class="add-meal-button" @click="openModal">식단 추가 +</button>
          <FoodAddModal ref="foodModal" @food-added="handleFoodAdded"/>
        </div>
      </div>
    </div>

    <!-- 영양소 분석 차트 -->
    <div class="nutrition-analysis">
      <div class="chart-section">
        <div class="chart-container">
          <Pie
              :data="chartData"
              :options="chartOptions"
          />
        </div>
        <div class="nutrition-details">
          <div class="nutrition-item carb">
            <div class="label">탄수화물</div>
            <div class="value">{{ nutritionDetails.carb.percentage }}%</div>
            <div class="detail">({{ nutritionDetails.carb.calories }}g)</div>
          </div>
          <div class="nutrition-item protein">
            <div class="label">단백질</div>
            <div class="value">{{ nutritionDetails.protein.percentage }}%</div>
            <div class="detail">({{ nutritionDetails.protein.calories }}g)</div>
          </div>
          <div class="nutrition-item fat">
            <div class="label">지방</div>
            <div class="value">{{ nutritionDetails.fat.percentage }}%</div>
            <div class="detail">({{ nutritionDetails.fat.calories }}g)</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, watch} from 'vue'
import {useRoute} from 'vue-router';
import {Chart as ChartJS, ArcElement, Tooltip, Legend} from 'chart.js'
import {Pie} from 'vue-chartjs'
import VueDatePicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import {format as formatDate} from 'date-fns'
import {ko} from 'date-fns/locale'
import axiosInstance from "@/plugins/axios.js";
import FoodAddModal from "@/components/mainBody/diet/FoodAddModal.vue";

ChartJS.register(ArcElement, Tooltip, Legend)

const foodModal = ref(null);

const openModal = () => {
  foodModal.value.isOpen = true;
};

const handleFoodAdded = (newDiet) => {
  diets.value.push(newDiet);
};

// 라우트와 상태 관리
const route = useRoute();
const currentMenu = ref('');
const currentView = ref('');
const diets = ref([]);

// 상태 관리
const now = new Date();
now.setHours(now.getHours() + 9); // UTC에서 한국 시간(KST)으로 변환
const selectedDate = ref(now.toISOString().split('T')[0]);
const selectedMealType = ref('all')
const targetCalories = ref(2600)

// 식사 타입 정의
const mealTypes = [
  {id: 'all', name: '전체', class: 'all'},
  {id: 0, name: '아침', class: 'breakfast'},
  {id: 1, name: '점심', class: 'lunch'},
  {id: 2, name: '저녁', class: 'dinner'},
  {id: 3, name: '간식', class: 'snack'}
];

// 삭제 함수
const deleteMeal = (dietNo) => {
  diets.value = diets.value.filter(diet => diet.dietNo !== dietNo);
};

// Chart.js 컴포넌트 등록
ChartJS.register(ArcElement, Tooltip, Legend)

// 차트 옵션
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: 'bottom',
      labels: {
        font: {
          size: 14
        },
        padding: 20
      }
    },
    tooltip: {
      callbacks: {
        label: (context) => {
          const label = context.label || '';
          const value = context.raw || 0;
          return `${label}: ${value}%`;
        }
      }
    }
  }
}

// 필터링된 식사의 영양소 계산
const filteredNutrition = computed(() => {
  return filteredMeals.value.reduce((total, diet) => {
    const carb = diet.foodQuantity * diet.food.foodCarb;
    const protein = diet.foodQuantity * diet.food.foodProtein;
    const fat = diet.foodQuantity * diet.food.foodFat;
    const totalNutrition = carb + protein + fat;

    return {
      carbCalories: total.carbCalories + carb,
      proteinCalories: total.proteinCalories + protein,
      fatCalories: total.fatCalories + fat,
      totalNutrition: total.totalNutrition + totalNutrition
    };
  }, {
    carbCalories: 0,
    proteinCalories: 0,
    fatCalories: 0,
    totalNutrition: 0
  });
});

// 차트 데이터 computed 속성으로 변경
const chartData = computed(() => {
  const nutrition = filteredNutrition.value;
  const total = nutrition.totalNutrition || 1;

  return {
    labels: ['탄수화물', '단백질', '지방'],
    datasets: [{
      data: [
        total === 0 ? 0 : Math.round((nutrition.carbCalories / total) * 100),
        total === 0 ? 0 : Math.round((nutrition.proteinCalories / total) * 100),
        total === 0 ? 0 : Math.round((nutrition.fatCalories / total) * 100)
      ],
      backgroundColor: ['#64B5F6', '#81C784', '#E57373'],
      borderWidth: 0,
      hoverBackgroundColor: ['#42A5F5', '#66BB6A', '#EF5350']
    }]
  };
});

// 영양소 상세 정보도 같은 방식으로 수정
const nutritionDetails = computed(() => {
  const nutrition = filteredNutrition.value;
  const total = nutrition.totalNutrition || 1;

  return {
    carb: {
      percentage: total === 0 ? 0 : Math.round((nutrition.carbCalories / total) * 100),
      calories: Math.round(nutrition.carbCalories)
    },
    protein: {
      percentage: total === 0 ? 0 : Math.round((nutrition.proteinCalories / total) * 100),
      calories: Math.round(nutrition.proteinCalories)
    },
    fat: {
      percentage: total === 0 ? 0 : Math.round((nutrition.fatCalories / total) * 100),
      calories: Math.round(nutrition.fatCalories)
    }
  };
});

// 필터링된 식사 목록
const filteredMeals = computed(() => {
  if (selectedMealType.value === 'all') {
    return diets.value;
  }
  return diets.value.filter(diet => diet.dietType === selectedMealType.value);
});

// 식사 타입 선택 메서드
const selectMealType = (type) => {
  selectedMealType.value = type;
};

// 필터링된 식사의 총 칼로리 계산
const totalFilteredCalories = computed(() => {
  return filteredMeals.value.reduce((total, diet) => {
    return total + (diet.foodQuantity * diet.food.foodCalorie);
  }, 0);
});

// 날짜 관련 상태 관리
const format = (date) => {
  return formatDate(date, 'yyyy-MM-dd', {locale: ko})
}

const currentDate = computed(() => {
  return format(selectedDate.value)
})

const getDayOfWeek = () => {
  const days = ['일', '월', '화', '수', '목', '금', '토']
  return days[new Date(selectedDate.value).getDay()]
}

const prevDay = () => {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() - 1)
  selectedDate.value = date.toISOString().split('T')[0]
}

const nextDay = () => {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() + 1)
  selectedDate.value = date.toISOString().split('T')[0]
}

const requestDietList = async () => {
  try {
    const formattedDate = format(currentDate.value); // 날짜를 문자열로 변환
    const {data} = await axiosInstance.get(`/diet/${formattedDate}`);
    diets.value = data;
  } catch (error) {
    console.error('식단 리스트를 불러오는데 실패했습니다:', error);
  }
};

// 라우트 변경 감지 및 초기화
watch(() => route.path, async (path) => {
      const [_, menu, view] = path.split('/');
      currentMenu.value = menu || '';
      currentView.value = view || '';
      if (menu === 'diet-manage') {
        await requestDietList();
        await getUserInfo();
        await loadActivities(format(selectedDate.value));
      }
    },
    {immediate: true}
);

const config = {
  headers: {
    'Authorization': `${localStorage.getItem('accessToken')}`,
    'Content-Type': 'application/json'
  }
};

// 날짜 변경 감지 및 식단 리스트 호출
watch(selectedDate, async (newDate) => {
  try {
    const formattedDate = format(newDate);
    const {data} = await axiosInstance.get(`/diet/${formattedDate}`, config);
    diets.value = data;
    await loadActivities(formattedDate);
  } catch (error) {
    console.error('식단 리스트를 불러오는데 실패했습니다:', error);
  }
});

// 저장하기
const saveDietList = async () => {
  try {
    const dietsToSave = diets.value.map(diet => ({
      ...(typeof diet.dietNo === 'number' && !isNaN(diet.dietNo) && { dietNo: diet.dietNo }),
      foodNo: diet.food.foodNo,
      dietType: diet.dietType,
      foodQuantity: diet.foodQuantity
    }));

    await axiosInstance.post(`/diet/${format(selectedDate.value)}`, dietsToSave, config);

    alert("식단이 저장되었습니다.")
    await requestDietList();
  } catch (error) {
    console.error('식단 저장 실패:', error);
  }
};

// 총대사량
const loadActivities = async (date) => {
  try {
    const response = await axiosInstance.get(`/activity/${date}`)

    // 기본값 설정
    targetCalories.value = gender.value ? 2000 : 2600;

    if(response.data.length > 0 && response.data[0].totalMetabolism) {
      targetCalories.value = response.data[0].totalMetabolism;
    }
  } catch (error) {
    console.error('활동 데이터 로드 실패:', error)
  }
}

// 유저 정보 호출 함수
const gender = ref(0)

const getUserInfo = async () => {
  try {
    const response = await axiosInstance.get('/user/userInfo');
    // 응답 데이터를 userInfo에 직접 할당
    gender.value = response.data.gender;
  } catch (error) {
    console.error('사용자 정보 로딩 실패:', error);
  }
}
</script>

<style scoped>
.diet-plan {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.calendar-section {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.date-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.calendar-nav button {
  background: none;
  border: none;
  font-size: 1.2em;
  cursor: pointer;
  padding: 0 10px;
}

.calorie-summary {
  background-color: #e3f2fd;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.big-number {
  font-size: 2em;
  color: #2196f3;
  font-weight: bold;
}

.meal-type-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.meal-button {
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9em;
  transition: all 0.3s ease;
}

.meal-button.active {
  transform: scale(1.05);
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.meal-button.all.active {
  background-color: #90CAF9;
  color: white;
}

.meal-button.breakfast.active {
  background-color: #A5D6A7;
  color: white;
}

.meal-button.lunch.active {
  background-color: #90CAF9;
  color: white;
}

.meal-button.dinner.active {
  background-color: #F48FB1;
  color: white;
}

.meal-button.snack.active {
  background-color: #FFCC80;
  color: white;
}

.meal-button.all {
  background-color: #E3F2FD;
}

.meal-button.breakfast {
  background-color: #FFF3E0;
}

.meal-button.lunch {
  background-color: #E3F2FD;
}

.meal-button.dinner {
  background-color: #FCE4EC;
}

.meal-button.snack {
  background-color: #E8F5E9;
}

.save-button {
  background-color: #64B5F6;
  color: white;
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9em;
  transition: background-color 0.2s;
  white-space: nowrap;
  margin-left: auto;
}

.save-button:hover {
  background-color: #42A5F5;
}

.meal-table {
  margin-bottom: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.chart-container {
  height: 300px;
  width: 300px;
  margin: 0 auto;
}

.nutrition-analysis {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-top: 20px;
}

.delete-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px 5px;
}

.nutrition-analysis {
  background-color: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-top: 20px;
}

.chart-section {
  display: flex;
  justify-content: space-around;
  align-items: center;
  gap: 40px;
}

.chart-container {
  width: 300px;
  height: 300px;
}

.nutrition-details {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.nutrition-item {
  padding: 15px;
  border-radius: 8px;
  color: white;
}

.nutrition-item.carb {
  background-color: #64B5F6;
}

.nutrition-item.protein {
  background-color: #81C784;
}

.nutrition-item.fat {
  background-color: #E57373;
}

.label {
  font-size: 16px;
  font-weight: bold;
}

.value {
  font-size: 24px;
  font-weight: bold;
  margin: 5px 0;
}

.detail {
  font-size: 12px;
  opacity: 0.9;
}


.table-footer {
  margin-top: 20px;
  padding: 20px 0;
}

.footer-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.add-meal-button {
  background-color: #64B5F6;
  color: white;
  padding: 10px 24px;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1em;
  display: flex;
  align-items: center;
  gap: 5px;
}

.add-meal-button:hover {
  opacity: 0.9;
}

tr {
  transition: background-color 0.2s;
}

/* 식사 구분별 배경색 */
tbody tr[data-meal-type="0"] {
  background-color: rgb(255, 255, 238);
}

tbody tr[data-meal-type="1"] {
  background-color: rgb(246, 249, 255);
}

tbody tr[data-meal-type="2"] {
  background-color: rgb(255, 247, 247);
}
tbody tr[data-meal-type="3"] {
  background-color: rgb(248, 255, 248);
}
</style>
