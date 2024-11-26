<template>
  <div class="activity-manage">
    <!-- 날짜 선택 섹션 -->
    <div class="date-section">
      <div class="date-display">
        <h2>{{ formatDate(currentDate) }}</h2>
        <span class="day-label">{{ getDayOfWeek(currentDate) }}요일</span>
      </div>
      <div class="date-controls">
        <button class="nav-btn" @click="changeDate(-1)">◀</button>
        <div class="date-numbers">
          <span v-for="offset in [-2, -1, 0, 1, 2]"
                :key="offset"
                :class="{ active: offset === 0 }"
                @click="changeDateByOffset(offset)">
            {{ getDayNumber(offset) }}
          </span>
        </div>
        <button class="nav-btn" @click="changeDate(1)">▶</button>
        <button class="calendar-btn" @click="showDatePicker = true">📅</button>
        <input v-if="showDatePicker"
               type="date"
               :value="formatDateForInput(currentDate)"
               @input="handleDateSelect"
               @blur="showDatePicker = false"
               class="date-picker">
      </div>
    </div>

    <!-- 대사량 정보 카드 -->
    <div class="info-card">
      <div class="metabolism-grid">
        <div class="metabolism-item">
          <span class="label">기초대사량</span>
          <span class="value">{{ basicMetabolism }} kcal</span>
        </div>
        <div class="metabolism-item">
          <span class="label">활동대사량</span>
          <span class="value">{{ activityMetabolism }} kcal</span>
        </div>
        <div class="metabolism-item">
          <span class="label">총 대사량</span>
          <span class="value">{{ totalMetabolism }} kcal</span>
        </div>
      </div>
      <div class="time-info">
        <span>활동시간: {{ totalActivityHours }}/24 시간</span>
        <div class="time-warning" v-if="totalActivityHours !== 24">
          하루 24시간을 모두 입력해야 저장할 수 있습니다.
        </div>
      </div>
    </div>

    <!-- 활동 목록 -->
    <div class="activities-section">
      <div class="activities-section">
        <div class="activities-list">
          <div v-for="(activity, index) in selectedActivities"
               :key="index"
               class="activity-item">
            <div class="activity-info">
          <span class="activity-name">
            {{ activity.activity?.actName }}
            ({{ activity.activity?.actInten}} MET
            / {{ (activity.activity?.actInten * lastWeight * activity.actTime).toFixed(1) }} kcal)
          </span>
              <div class="time-edit">
                <input
                    type="number"
                    v-model.number="activity.actTime"
                    min="0"
                    max="24"
                    step="0.5"
                    class="modern-input"
                    @change="validateTotalTime"
                >
                <span class="time-unit">시간</span>
              </div>
            </div>
            <button @click="removeActivity(index)" class="remove-btn">×</button>
          </div>
        </div>
      </div>
      <div class="button-group">
        <button @click="isModalOpen = true" class="modern-button add-btn">
          활동 추가
        </button>
        <button
            @click="saveActivities"
            class="modern-button save-btn"
            :disabled="!canSaveActivities"
        >
          저장하기
        </button>
      </div>
    </div>

    <!-- 활동 추가 모달 -->
    <ActivityModal
        v-model:isOpen="isModalOpen"
        @activity-added="handleActivityAdded"
    />
  </div>
  <div class="template-section">
    <h3 class="template-title">추천 활동 템플릿</h3>
    <div class="template-grid">
      <div v-for="(template, index) in activityTemplates"
           :key="index"
           class="template-card"
           @click="applyTemplate(template)">
        <div class="template-header">
          <h4>{{ template.name }}</h4>
          <span class="template-icon">📋</span>
        </div>
        <div class="template-activities">
          <div v-for="(act, actIndex) in template.activities"
               :key="actIndex"
               class="template-activity-item">
            <span class="activity-name">{{ act.actName }}</span>
            <span class="activity-time">{{ act.actTime }}시간</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, watch, onMounted, reactive} from 'vue'
import axiosInstance from "@/plugins/axios.js"
import ActivityModal from './ActivityModal.vue'

// 상태 관리
const currentDate = ref(new Date())
const showDatePicker = ref(false)
const isModalOpen = ref(false)
const selectedActivities = ref([])
const basicMetabolism = ref(1500)

const openActivityModal = () => {
  isModalOpen.value = true  // .isOpen 제거
}

// 날짜 관련 메서드들
const changeDate = (days) => {
  const newDate = new Date(currentDate.value)
  newDate.setDate(newDate.getDate() + days)
  currentDate.value = newDate
}

const changeDateByOffset = (offset) => {
  changeDate(offset)
}

const handleDateSelect = (event) => {
  currentDate.value = new Date(event.target.value)
  showDatePicker.value = false
}

const formatDateForInput = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getDayOfWeek = (date) => {
  const days = ['일', '월', '화', '수', '목', '금', '토']
  return days[date.getDay()]
}

// Methods
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getDayNumber = (offset) => {
  const date = new Date(currentDate.value)
  date.setDate(date.getDate() + offset)
  return date.getDate()
}

const activityApi = {
  getActivities(date) {
    return axiosInstance.get(`/activity/${date}`)
  },
  saveActivities(date, activities) {
    return axiosInstance.post(`/activity/${date}`, activities)
  }
}

const totalActivityHours = computed(() => {
  return selectedActivities.value.reduce((sum, act) => sum + act.actTime, 0)
})

// computed 속성 수정
const canSaveActivities = computed(() => {
  return selectedActivities.value.length > 0 && totalActivityHours.value === 24
})

// ... (기존 날짜 관련 메서드들 유지)

const handleActivityAdded = (newActivity) => {
  selectedActivities.value.push(newActivity)
}

const removeActivity = (index) => {
  selectedActivities.value.splice(index, 1)
}

// saveActivities 함수 수정
const saveActivities = async () => {
  try {
    if (totalActivityHours.value !== 24) {
      alert('하루 24시간을 모두 입력해야 합니다.')
      return
    }

    const activities = selectedActivities.value.map(activity => ({
      actNo: activity.actNo,
      actTime: activity.actTime,
      actDate: formatDate(currentDate.value),
      totalMetabolism: totalMetabolism.value,
    }))

    await activityApi.saveActivities(formatDate(currentDate.value), activities)
    alert('활동이 저장되었습니다.')
    await loadActivities(formatDate(currentDate.value))
  } catch (error) {
    console.error('활동 저장 실패:', error)
    alert('활동 저장에 실패했습니다.')
  }
}

const loadActivities = async (date) => {
  try {
    const {data} = await activityApi.getActivities(date)
    selectedActivities.value = data
  } catch (error) {
    console.error('활동 데이터 로드 실패:', error)
  }
}

watch(() => currentDate.value, async (newDate) => {
  await loadActivities(formatDate(newDate))
})

onMounted(async () => {
  try {
    await loadActivities(formatDate(currentDate.value));
    await loadWeightData();
    await getUserInfo();

    if (userInfo && lastWeight.value) {
      basicMetabolism.value =
          (10 * lastWeight.value) +
          (6.25 * userInfo.height) -
          (5 * 10) -
          (161 * userInfo.gender);
    }
  } catch (error) {
    console.error('초기화 중 오류 발생:', error);
  }
});


// Computed Properties
const activityMetabolism = computed(() => {
  return selectedActivities.value.reduce((total, activity) => {
    if (!activity.activity) return total
    return total + (activity.activity.actInten * lastWeight.value * activity.actTime)
  }, 0).toFixed(0)
})

const totalMetabolism = computed(() => {
  return (Number(activityMetabolism.value)).toFixed(0)
})

// 체중 데이터 로드 함수
const lastWeight = ref(65);

const loadWeightData = async () => {
  try {
    const response = await axiosInstance.get(`/calendar/weight-data`);
    if (Array.isArray(response.data) && response.data.length > 0) {
      // 첫 번째 몸무게 저장
      lastWeight.value = response.data[0].weight;
    }
  } catch (error) {
    console.error('데이터 로딩 실패:', error);
    lastWeight.value = null;
  }
};


// 유저 정보 호출 함수
const userInfo = reactive({
  birthday: '',
  gender: '',
  height: '',
});

const getUserInfo = async () => {
  try {
    const response = await axiosInstance.get('/user/userInfo');
    // 응답 데이터를 userInfo에 직접 할당
    userInfo.birthday = response.data.birthday;
    userInfo.gender = response.data.gender;
    userInfo.height = response.data.height;
  } catch (error) {
    console.error('사용자 정보 로딩 실패:', error);
  }
}

const activityTemplates = [
  {
    name: "일반 사무직 템플릿",
    activities: [
      { actName: "수면", actInten: 0.95, actTime: 7 },
      { actName: "좌식 업무", actInten: 1.3, actTime: 8 },
      { actName: "가벼운 걷기", actInten: 2.5, actTime: 1 },
      { actName: "식사 및 휴식", actInten: 1.5, actTime: 3 },
      { actName: "가벼운 운동", actInten: 3.0, actTime: 1 },
      { actName: "TV 시청/독서", actInten: 1.0, actTime: 4 }
    ]
  },
  {
    name: "활동적인 생활 템플릿",
    activities: [
      { actName: "수면", actInten: 0.95, actTime: 7 },
      { actName: "중강도 운동", actInten: 5.0, actTime: 2 },
      { actName: "일상 활동", actInten: 2.0, actTime: 6 },
      { actName: "식사 및 휴식", actInten: 1.5, actTime: 3 },
      { actName: "가벼운 걷기", actInten: 2.5, actTime: 2 },
      { actName: "여가 활동", actInten: 1.8, actTime: 4 }
    ]
  },
  {
    name: "재택근무 템플릿",
    activities: [
      { actName: "수면", actInten: 0.95, actTime: 8 },
      { actName: "재택 업무", actInten: 1.2, actTime: 8 },
      { actName: "집안일", actInten: 2.5, actTime: 2 },
      { actName: "식사 및 휴식", actInten: 1.5, actTime: 3 },
      { actName: "스트레칭/요가", actInten: 2.5, actTime: 1 },
      { actName: "휴식", actInten: 1.0, actTime: 2 }
    ]
  },
];

const applyTemplate = (template) => {
  // 기존 활동 초기화
  selectedActivities.value = []

  // 템플릿의 활동들을 selectedActivities에 추가
  template.activities.forEach(act => {
    selectedActivities.value.push({
      activity: {
        actName: act.actName,
        actInten: act.actInten
      },
      actTime: act.actTime,
      actNo: null // 서버에서 부여될 ID
    })
  })
}
</script>

<style scoped>
.activity-manage {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 날짜 섹션 */
.date-section {
  margin-bottom: 30px;
}

.date-display {
  text-align: center;
  margin-bottom: 15px;
}

.date-display h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.day-label {
  color: #666;
  font-size: 16px;
}

.date-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  position: relative;
}

.date-numbers {
  display: flex;
  gap: 10px;
}

.date-numbers span {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 20px;
  transition: all 0.3s;
}

.date-numbers span.active {
  background-color: #b1def0;
  color: white;
}

.nav-btn, .calendar-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  font-size: 16px;
}

/* 대사량 정보 카드 */
.info-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.metabolism-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 15px;
}

.metabolism-item {
  text-align: center;
}

.label {
  display: block;
  color: #666;
  margin-bottom: 5px;
}

.value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

/* 활동 목록 */
.activities-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #eee;
}

.activity-info {
  display: flex;
  justify-content: space-between;
  flex-grow: 1;
  margin-right: 15px;
}

.remove-btn {
  background: none;
  border: none;
  color: #ff4444;
  cursor: pointer;
  font-size: 20px;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.action-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
}

.add-btn {
  background-color: #b1def0;
  color: white;
}

.save-btn {
  background-color: #4CAF50;
  color: white;
}

.action-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* 모달 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 10px;
  padding: 20px;
  width: 90%;
  max-width: 500px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.category-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  overflow-x: auto;
}

.category-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  background: #f0f0f0;
  cursor: pointer;
  white-space: nowrap;
}

.category-btn.active {
  background: #b1def0;
  color: white;
}

.activities-grid {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 20px;
}

.activity-option {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  border-radius: 5px;
  cursor: pointer;
  margin-bottom: 5px;
}

.activity-option:hover {
  background: #f0f0f0;
}

.activity-option.selected {
  background: #b1def0;
  color: white;
}

.duration-input {
  margin-bottom: 20px;
}

.duration-input input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  margin-top: 5px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-footer button {
  padding: 8px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.cancel-btn {
  background: #f0f0f0;
}

.confirm-btn {
  background: #b1def0;
  color: white;
}

.confirm-btn:disabled {
  background: #cccccc;
  cursor: not-allowed;
}

.date-picker {
  position: absolute;
  top: 100%;
  right: 0;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background: white;
  z-index: 1000;
}

.time-warning {
  color: #f44336;
  font-size: 14px;
  margin-top: 5px;
}

.time-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.template-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-top: 30px;
}

.template-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.template-card {
  background: linear-gradient(145deg, #ffffff, #f5f7fa);
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(177, 222, 240, 0.2);
}

.template-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(177, 222, 240, 0.3);
  background: linear-gradient(145deg, #b1def0, #98c9e3);
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.template-header h4 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #2c3e50;
}

.template-card:hover .template-header h4 {
  color: white;
}

.template-icon {
  font-size: 20px;
  opacity: 0.7;
}

.template-activities {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.template-activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  font-size: 14px;
}

.template-card:hover .template-activity-item {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.activity-name {
  font-weight: 500;
}

.activity-time {
  color: #666;
  font-size: 13px;
}

.template-card:hover .activity-time {
  color: rgba(255, 255, 255, 0.8);
}

.time-edit {
  display: flex;
  align-items: center;
  gap: 8px;
}

.modern-input {
  width: 70px;
  font-size: 16px;
  color: #333;
  background-color: #fff;
  padding: 8px 12px;
  border: 2px solid transparent;
  border-radius: 8px;
  outline: none;
  transition: all 0.3s ease;
  background-image: linear-gradient(#fff, #fff),
  linear-gradient(45deg, #b1def0, #8677e3);
  background-origin: border-box;
  background-clip: padding-box, border-box;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.modern-input:focus {
  box-shadow: 0 0 10px rgba(177, 222, 240, 0.5);
  transform: translateY(-1px);
}

.modern-input:hover {
  background-image: linear-gradient(#fff, #fff),
  linear-gradient(45deg, #4880cc, #b1def0);
}

.time-unit {
  color: #666;
  font-size: 14px;
  font-weight: 500;
}
.button-group {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.modern-button {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  background-size: 200% auto;
  color: white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.add-btn {
  background-image: linear-gradient(45deg, #2196f3, #b1def0);
}

.save-btn {
  background-image: linear-gradient(45deg, #4CAF50, #81c784);
}

.modern-button:hover {
  background-position: right center;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.modern-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modern-button:disabled {
  background: #e0e0e0;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.remove-btn {
  background: none;
  border: none;
  color: #ff5252;
  cursor: pointer;
  font-size: 20px;
  transition: all 0.2s ease;
  padding: 8px;
  border-radius: 50%;
}

.remove-btn:hover {
  background: rgba(255, 82, 82, 0.1);
  transform: rotate(90deg);
}

</style>