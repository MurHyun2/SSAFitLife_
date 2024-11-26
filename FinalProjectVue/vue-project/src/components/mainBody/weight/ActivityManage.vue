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
          <span class="value">{{ dummyBasicMetabolism }} kcal</span>
        </div>
        <div class="metabolism-item">
          <span class="label">활동대사량</span>
          <span class="value">{{ calculateActivityMetabolism() }} kcal</span>
        </div>
        <div class="metabolism-item">
          <span class="label">총 대사량</span>
          <span class="value">{{ calculateTotalMetabolism() }} kcal</span>
        </div>
      </div>
      <div class="time-info">
        <span>활동시간: {{ totalActivityHours }}/24 시간</span>
      </div>
    </div>

    <!-- 활동 목록 -->
    <div class="activities-section">
      <div class="activities-list">
        <div v-for="(activity, index) in selectedActivities"
             :key="index"
             class="activity-item">
          <div class="activity-info">
            <span class="activity-name">{{ activity.actName }}</span>
            <span class="activity-duration">{{ activity.actTime }}시간</span>
          </div>
          <button @click="removeActivity(index)" class="remove-btn">×</button>
        </div>
      </div>
      <div class="button-group">
        <button @click="openActivityModal" class="action-btn add-btn">
          활동 추가
        </button>
        <button @click="saveActivities"
                class="action-btn save-btn"
                :disabled="!canSaveActivities">
          저장하기
        </button>
      </div>
    </div>

    <!-- 활동 추가 모달 컴포넌트 -->
    <ActivityModal
        v-model:isOpen="isActivityModalOpen"
        @activity-added="handleActivityAdded"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import axiosInstance from "@/plugins/axios.js"
import ActivityModal from './ActivityModal.vue'

// 상태 관리
const currentDate = ref(new Date())
const showDatePicker = ref(false)
const activityModal = ref(null)
const selectedActivities = ref([])
const dummyBasicMetabolism = 1500

// API 함수
const activityApi = {
  getActivities(date) {
    return axiosInstance.get(`/activity/${date}`)
  },
  saveActivities(date, activities) {
    return axiosInstance.post(`/activity/${date}`, activities)
  }
}

// Computed Properties
const totalActivityHours = computed(() => {
  return selectedActivities.value.reduce((sum, act) => sum + act.actTime, 0)
})

const canSaveActivities = computed(() => {
  return selectedActivities.value.length > 0 && totalActivityHours.value <= 24
})

// Methods
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const formatDateForInput = (date) => formatDate(date)

const getDayOfWeek = (date) => {
  const days = ['일', '월', '화', '수', '목', '금', '토']
  return days[date.getDay()]
}

const getDayNumber = (offset) => {
  const date = new Date(currentDate.value)
  date.setDate(date.getDate() + offset)
  return date.getDate()
}

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

const calculateActivityMetabolism = () => {
  return selectedActivities.value.reduce((total, activity) => {
    return total + (activity.actInten * 3.5 * 3.5 * 70 * (activity.actTime / 24))
  }, 0).toFixed(0)
}

const calculateTotalMetabolism = () => {
  return (Number(calculateActivityMetabolism()) + dummyBasicMetabolism).toFixed(0)
}

const openActivityModal = () => {
  activityModal.value.isOpen = true
}

const handleActivityAdded = (newActivity) => {
  selectedActivities.value.push(newActivity)
}

const removeActivity = (index) => {
  selectedActivities.value.splice(index, 1)
}

const saveActivities = async () => {
  try {
    const activities = selectedActivities.value.map(activity => ({
      actNo: activity.actNo,
      actTime: activity.actTime,
      actDate: formatDate(currentDate.value)
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
    const { data } = await activityApi.getActivities(date)
    selectedActivities.value = data
  } catch (error) {
    console.error('활동 데이터 로드 실패:', error)
  }
}

// Watchers
watch(() => currentDate.value, async (newDate) => {
  await loadActivities(formatDate(newDate))
})

// Lifecycle Hooks
onMounted(async () => {
  await loadActivities(formatDate(currentDate.value))
})
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
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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

/* 템플릿 섹션 */
.template-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 15px;
}

.template-card {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
}

.template-card:hover {
  transform: translateY(-2px);
  background-color: #b1def0;
  color: white;
}

/* 모달 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
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
</style>