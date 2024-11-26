<template>
  <div v-if="isOpen" class="modal-overlay">
    <div class="modal-content">
      <!-- 탭 메뉴 -->
      <div class="tab-menu">
        <button
            :class="['tab-button', { active: activeTab === '활동 목록' }]"
            @click="activeTab = '활동 목록'"
        >
          활동 목록
        </button>
        <button
            :class="['tab-button', { active: activeTab === '활동 등록/수정' }]"
            @click="activeTab = '활동 등록/수정'"
        >
          활동 등록/수정
        </button>
        <button
            :class="['tab-button', { active: activeTab === '등록한 활동' }]"
            @click="activeTab = '등록한 활동'"
        >
          등록한 활동
        </button>
      </div>

      <!-- 활동 목록 폼 -->
      <div v-if="activeTab === '활동 목록'">
        <div class="search-section">
          <input
              type="text"
              v-model="searchKeyword"
              placeholder="활동 검색"
              class="search-input"
              @input="searchActivity"
          />
        </div>

        <div class="search-results" @scroll="handleScroll">
          <div v-for="activity in searchResults"
               :key="activity.actNo"
               class="activity-item"
               :class="{ 'selected': selectedActivity?.actNo === activity.actNo }"
               @click="selectActivity(activity)"
          >
            <div class="activity-info">
              <div class="activity-name">{{ activity.actName }}</div>
              <div class="activity-details">
                <span>{{ activity.actInten }} MET / {{ activity.actInten * lastWeight }} kcal</span>
              </div>
            </div>
            <button class="add-button" @click="selectActivity(activity)">+</button>
          </div>
          <div v-if="loading" class="loading">로딩 중...</div>
        </div>

        <!-- 선택된 활동 정보 -->
        <div v-if="selectedActivity" class="quantity-section">
          <div class="selected-activity-info">
            <span>{{ selectedActivity.actName }}</span>
            <div class="duration-input">
              <input
                  type="number"
                  v-model="duration"
                  min="0.5"
                  max="24"
                  step="0.5"
              />
              <span>시간</span>
            </div>
          </div>
        </div>

        <div class="modal-buttons">
          <button @click="closeModal" class="cancel-btn">취소</button>
          <button
              @click="addActivity"
              class="confirm-btn"
              :disabled="!canAdd"
          >
            추가하기
          </button>
        </div>
      </div>

      <!-- 활동 등록/수정 폼 -->
      <div v-if="activeTab === '활동 등록/수정'" class="activity-register-form">
        <div class="form-group">
          <label>활동명</label>
          <input type="text" v-model="activityData.actName" placeholder="활동명을 입력하세요"/>
        </div>
        <div class="form-group">
          <label>MET( 1 MET = {{ lastWeight }} kcal )</label>
          <input type="number" v-model="activityData.actInten" placeholder="0"/>
        </div>
        <div class="modal-buttons">
          <button @click="closeModal" class="cancel-btn">취소</button>
          <button
              @click="registerActivity"
              class="confirm-btn"
              :disabled="!isValidActivity"
          >
            {{ isEditMode ? '수정하기' : '등록하기' }}
          </button>
        </div>
      </div>

      <!-- 등록한 활동 목록 -->
      <div v-if="activeTab === '등록한 활동'">
        <div class="search-section">
          <input
              type="text"
              v-model="registeredSearchKeyword"
              placeholder="등록한 활동 검색"
              class="search-input"
              @input="searchRegisteredActivity"
          />
        </div>
        <div class="search-results" @scroll="handleRegisteredScroll">
          <div v-for="activity in registeredActivities"
               :key="activity.actNo"
               class="activity-item"
               @click="selectActivity(activity)"
          >
            <div class="activity-info">
              <div class="activity-name">{{ activity.actName }}</div>
              <div class="activity-details">
                <span class="met-badge">{{ activity.actInten }} MET / {{ activity.actInten * lastWeight }} kcal</span>
              </div>
            </div>
            <div class="action-buttons">
              <button class="add-button">+</button>
              <button class="edit-button" @click="editActivity(activity)">수정</button>
              <button class="delete-button" @click="deleteActivity(activity)">삭제</button>
            </div>
          </div>
        </div>

        <!-- 선택된 활동 정보 추가 -->
        <div v-if="selectedActivity" class="quantity-section">
          <div class="selected-activity-info">
            <span>{{ selectedActivity.actName }} </span>
            <div class="duration-input">
              <input
                  type="number"
                  v-model="duration"
                  min="0.5"
                  max="24"
                  step="0.5"
              />
              <span>시간</span>
            </div>
          </div>
        </div>
        <div class="modal-buttons">
          <button @click="closeModal" class="cancel-btn">취소</button>
          <button
              @click="addActivity"
              class="confirm-btn"
              :disabled="!canAdd"
          >
            추가하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import axiosInstance from '@/plugins/axios'

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['update:isOpen', 'activity-added'])

// 상태 관리
const activeTab = ref('활동 목록')
const searchKeyword = ref('')
const selectedActivity = ref(null)
const duration = ref(1)
const activities = ref([])
const searchResults = ref([])
const loading = ref(false)
const page = ref(1)
const limit = 10
const pageSize = 10


// 활동 등록/수정 관련
const isEditMode = ref(false)
const editingActNo = ref(null)
const activityData = ref({
  actName: '',
  actInten: 0,
})

// 등록한 활동 관련
const registeredSearchKeyword = ref('')
const registeredActivities = ref([])
const registeredPage = ref(1)
const registeredLoading = ref(false)
const registeredHasMore = ref(true)
const allRegisteredActivities = ref([]) // 전체 목록 저장용 추가

// Computed Properties
const canAdd = computed(() => {
  return selectedActivity.value && duration.value > 0 && duration.value <= 24
})

const isValidActivity = computed(() => {
  return activityData.value.actName && activityData.value.actInten > 0
})

// Methods
const loadInitialData = async () => {
  try {
    const response = await axiosInstance.get('/activity/list')
    activities.value = response.data
    searchResults.value = activities.value.slice(0, limit)
  } catch (error) {
    console.error('활동 데이터 로드 실패:', error)
  }
}

const handleScroll = (e) => {
  const element = e.target
  const { scrollTop, scrollHeight, clientHeight } = element
  if (scrollHeight - scrollTop <= clientHeight + 10) {
    loadMore()
  }
}

const loadMore = () => {
  if (loading.value) return
  loading.value = true

  const nextPage = page.value + 1
  const start = (nextPage - 1) * limit
  const end = start + limit

  const currentResults = searchKeyword.value
      ? activities.value.filter(activity =>
          activity.actName.toLowerCase().includes(searchKeyword.value.toLowerCase())
      )
      : activities.value

  if (start < currentResults.length) {
    const newItems = currentResults.slice(start, end)
    searchResults.value = [...searchResults.value, ...newItems]
    page.value = nextPage
  }

  loading.value = false
}

const selectActivity = (activity) => {
  selectedActivity.value = activity
  duration.value = 1
}

const addActivity = () => {
  if (canAdd.value) {
    emit('activity-added', {
      actNo: selectedActivity.value.actNo,
      actTime: duration.value,
      activity: {  // activity 객체 추가
        actNo: selectedActivity.value.actNo,
        actName: selectedActivity.value.actName,
        actInten: selectedActivity.value.actInten
      }
    })
    selectedActivity.value = null
    duration.value = 1
  }
}

const registerActivity = async () => {
  try {
    if (isEditMode.value) {
      await axiosInstance.put(`/activity/${editingActNo.value}`, activityData.value)
      alert('활동이 수정되었습니다.')
    } else {
      await axiosInstance.post('/activity', activityData.value)
      alert('활동이 등록되었습니다.')
    }

    // 폼 초기화
    activityData.value = {
      actName: '',
      actInten: 0,
    }
    isEditMode.value = false
    editingActNo.value = null

    // 활동 목록 새로고침 후 탭 전환
    await loadRegisteredActivities(true)  // true를 전달하여 처음부터 다시 로드
    activeTab.value = '등록한 활동'

  } catch (error) {
    console.error('활동 처리 실패:', error)
    alert(isEditMode.value ? '활동 수정에 실패했습니다.' : '활동 등록에 실패했습니다.')
  }
}

const loadRegisteredActivities = async () => {
  try {
    const { data } = await axiosInstance.get('/activity/registered')
    allRegisteredActivities.value = data  // 전체 데이터 저장
    registeredActivities.value = data     // 화면에 표시할 데이터
    await loadWeightData();
  } catch (error) {
    console.error('등록한 활동 로드 실패:', error)
  }
}

// 검색어 변경 감지
watch(registeredSearchKeyword, () => {
  searchRegisteredActivity()
})

// 등록한 활동 검색
const searchRegisteredActivity = () => {
  if (!registeredSearchKeyword.value) {
    registeredActivities.value = allRegisteredActivities.value
    return
  }

  const keyword = registeredSearchKeyword.value.toLowerCase()
  registeredActivities.value = allRegisteredActivities.value.filter(activity =>
      activity.actName.toLowerCase().includes(keyword)
  )
}

// 등록한 활동 수정
const editActivity = (activity) => {
  isEditMode.value = true
  editingActNo.value = activity.actNo
  activityData.value = {
    actName: activity.actName,
    actInten: activity.actInten,
    isShared: activity.isShared
  }
  activeTab.value = '활동 등록/수정'  // 탭 전환
}

// 등록한 활동 삭제
const deleteActivity = async (activity) => {
  if (confirm('정말 삭제하시겠습니까?')) {
    try {
      await axiosInstance.delete(`/activity/${activity.actNo}`)
      await loadRegisteredActivities(true)
      alert('활동이 삭제되었습니다.')
    } catch (error) {
      console.error('활동 삭제 실패:', error)
      alert('활동 삭제에 실패했습니다.')
    }
  }
}

// 무한 스크롤 핸들러
const handleRegisteredScroll = async (e) => {
  const { scrollTop, scrollHeight, clientHeight } = e.target
  if (scrollHeight - scrollTop <= clientHeight + 50) {
    await loadRegisteredActivities()
  }
}

const closeModal = () => {
  emit('update:isOpen', false)
  selectedActivity.value = null
  duration.value = 1
  searchKeyword.value = ''
  searchResults.value = []
  page.value = 1
  activityData.value = {
    actName: '',
    actInten: 0,
  }
}

// Watchers
watch(() => props.isOpen, async (newValue) => {
  if (newValue) {
    await loadInitialData()
    await loadRegisteredActivities()
  }
})

watch(activeTab, () => {
  selectedActivity.value = null
  duration.value = 1
})

const searchActivity = () => {
  page.value = 1
  if (searchKeyword.value) {
    const filtered = activities.value.filter(activity =>
        activity.actName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
    searchResults.value = filtered.slice(0, limit)
  } else {
    searchResults.value = activities.value.slice(0, limit)
  }
}
watch(() => props.isOpen, async (newValue) => {
  if (newValue) {
    await loadRegisteredActivities(true)
  }
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
</script>

<style scoped>
.modal-overlay {
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
  width: 90%;
  max-width: 500px;
  border-radius: 12px;
  padding: 20px;
  max-height: 85vh;
  overflow-y: auto;
}

.tab-menu {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.tab-button {
  padding: 12px 24px;
  border: none;
  border-radius: 0;
  cursor: pointer;
  background: transparent;
  font-size: 16px;
  color: #666;
}

.tab-button.active {
  background: transparent;
  color: #2196f3;
  font-weight: bold;
  border-bottom: 2px solid #2196f3;
}

.search-input {
  width: 90%;
  padding: 12px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 16px;
}

.activity-item {
  display: grid;
  grid-template-columns: 1fr auto auto;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  gap: 20px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.activity-item:hover {
  background-color: #f5f5f5;
}

.activity-item.selected {
  background-color: #e3f2fd;
  border-left: 3px solid #2196f3;
}

.activity-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.activity-name {
  font-size: 16px;
  font-weight: bold;
}

.activity-details {
  color: #666;
  font-size: 14px;
}

.add-button {
  background: #2196f3;
  color: white;
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 20px;
  line-height: 1;
}

.quantity-section {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.selected-activity-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
}

.duration-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.duration-input input {
  width: 50px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: right;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.confirm-btn, .cancel-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.confirm-btn {
  background: #2196f3;
  color: white;
}

.cancel-btn {
  background: #f0f0f0;
}

.search-results {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
}

.activity-register-form {
  padding: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.form-group input {
  width: 90%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
}

.nutrition-inputs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.confirm-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-button, .delete-button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.edit-button {
  background: #4CAF50;
  color: white;
}

.delete-button {
  background: #f44336;
  color: white;
}

.edit-button:hover {
  background: #45a049;
}

.delete-button:hover {
  background: #da190b;
}
</style>