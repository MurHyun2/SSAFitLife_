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
                  <span 
                      v-for="offset in [-2, -1, 0, 1, 2]" 
                      :key="offset"
                      :class="{ active: offset === 0 }"
                      @click="changeDateByOffset(offset)"
                  >
                      {{ getDayNumber(offset) }}
                  </span>
              </div>
              <button class="nav-btn" @click="changeDate(1)">▶</button>
              <button class="calendar-btn" @click="showDatePicker = true">📅</button>
              <input 
                  v-if="showDatePicker"
                  type="date"
                  :value="formatDateForInput(currentDate)"
                  @input="handleDateSelect"
                  @blur="showDatePicker = false"
                  class="date-picker"
              >
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
                      <span class="activity-name">{{ activity.name }}</span>
                      <span class="activity-duration">{{ activity.duration }}시간</span>
                  </div>
                  <button @click="removeActivity(index)" class="remove-btn">×</button>
              </div>
          </div>
          <div class="button-group">
              <button @click="isActivityModalOpen = true" class="action-btn add-btn">
                  활동 추가
              </button>
              <button 
                  @click="saveActivities" 
                  class="action-btn save-btn"
                  :disabled="!canSaveActivities">
                  저장하기
              </button>
          </div>
      </div>

      <!-- 템플릿 섹션 -->
      <div class="template-section">
          <h3>추천 템플릿</h3>
          <div class="template-grid">
              <div v-for="temp in predefinedTemplates" 
                   :key="temp.name" 
                   class="template-card"
                   @click="loadPredefinedTemplate(temp)">
                  <h4>{{ temp.name }}</h4>
                  <p>{{ temp.description }}</p>
              </div>
          </div>
      </div>

      <!-- 활동 추가 모달 -->
      <div v-if="isActivityModalOpen" class="modal">
          <div class="modal-content">
              <div class="modal-header">
                  <h3>활동 추가</h3>
                  <button class="close-btn" @click="isActivityModalOpen = false">×</button>
              </div>
              <div class="modal-body">
                  <div class="category-tabs">
                      <button 
                          v-for="cat in categories" 
                          :key="cat"
                          :class="['category-btn', { active: selectedCategory === cat }]"
                          @click="selectedCategory = cat"
                      >
                          {{ cat }}
                      </button>
                  </div>
                  <div class="activities-grid">
                      <div v-for="act in filteredActivities" 
                           :key="act.id" 
                           :class="['activity-option', { selected: selectedActivity?.id === act.id }]"
                           @click="selectActivity(act)">
                          <span>{{ act.name }}</span>
                          <span class="met-value">{{ act.met }} MET</span>
                      </div>
                  </div>
                  <div class="duration-input">
                      <label>활동 시간</label>
                      <input 
                          type="number" 
                          v-model="duration" 
                          min="0.5" 
                          max="24" 
                          step="0.5"
                      >
                      <span class="unit">시간</span>
                  </div>
              </div>
              <div class="modal-footer">
                  <button class="cancel-btn" @click="isActivityModalOpen = false">취소</button>
                  <button 
                      class="confirm-btn"
                      :disabled="!canAddActivity"
                      @click="addActivity"
                  >
                      추가하기
                  </button>
              </div>
          </div>
      </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 상태 관리
const currentDate = ref(new Date())
const showDatePicker = ref(false)
const isActivityModalOpen = ref(false)
const selectedCategory = ref('수면')
const selectedActivity = ref(null)
const duration = ref(1)
const selectedActivities = ref([])
const dummyBasicMetabolism = 1500 // 더미 데이터

// 카테고리 및 활동 데이터
const categories = ['수면', '업무', '운동', '식사', '휴식']
const activities = [
  { id: 1, name: '수면', met: 0.95, category: '수면' },
  { id: 2, name: '사무work', met: 1.5, category: '업무' },
  { id: 3, name: '걷기', met: 3.5, category: '운동' },
  { id: 4, name: '달리기', met: 8.0, category: '운동' },
  { id: 5, name: '자전거', met: 5.0, category: '운동' },
  { id: 6, name: '식사', met: 1.5, category: '식사' },
  { id: 7, name: 'TV시청', met: 1.2, category: '휴식' },
  { id: 8, name: '독서', met: 1.3, category: '휴식' }
]

// 템플릿 데이터
const predefinedTemplates = [
  {
      name: '일반 사무직',
      description: '일반적인 사무직 하루 일과',
      activities: [
          { id: 1, name: '수면', met: 0.95, duration: 7 },
          { id: 2, name: '사무work', met: 1.5, duration: 8 },
          { id: 6, name: '식사', met: 1.5, duration: 2 },
          { id: 7, name: 'TV시청', met: 1.2, duration: 7 }
      ]
  },
  {
      name: '활동적인 일상',
      description: '운동이 포함된 활동적인 일과',
      activities: [
          { id: 1, name: '수면', met: 0.95, duration: 7 },
          { id: 3, name: '걷기', met: 3.5, duration: 2 },
          { id: 2, name: '사무work', met: 1.5, duration: 8 },
          { id: 6, name: '식사', met: 1.5, duration: 2 },
          { id: 8, name: '독서', met: 1.3, duration: 5 }
      ]
  }
]

// 계산된 속성
const filteredActivities = computed(() => {
  return activities.filter(act => act.category === selectedCategory.value)
})

const totalActivityHours = computed(() => {
  return selectedActivities.value.reduce((sum, act) => sum + act.duration, 0)
})

const canSaveActivities = computed(() => {
  return selectedActivities.value.length > 0 && totalActivityHours.value <= 24
})

const canAddActivity = computed(() => {
  return selectedActivity.value && duration.value > 0 && duration.value <= 24
})

// 메서드
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
      return total + (activity.met * 3.5 * 3.5 * 70 * (activity.duration / 24))
  }, 0).toFixed(0)
}

const calculateTotalMetabolism = () => {
  return (Number(calculateActivityMetabolism()) + dummyBasicMetabolism).toFixed(0)
}

const selectActivity = (activity) => {
  selectedActivity.value = activity
}

const addActivity = () => {
  if (canAddActivity.value) {
      selectedActivities.value.push({
          ...selectedActivity.value,
          duration: duration.value
      })
      isActivityModalOpen.value = false
      selectedActivity.value = null
      duration.value = 1
  }
}

const removeActivity = (index) => {
  selectedActivities.value.splice(index, 1)
}

const loadPredefinedTemplate = (template) => {
  selectedActivities.value = [...template.activities]
}

const saveActivities = () => {
  // 임시 저장 동작
  console.log('저장된 활동:', selectedActivities.value)
  alert('활동이 저장되었습니다.')
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