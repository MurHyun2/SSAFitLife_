<template>
  <div v-if="isOpen" class="modal-overlay">
    <div class="modal-content">
      <!-- 모달 헤더 -->
      <div class="modal-header">
        <h3>활동 추가</h3>
        <button class="close-btn" @click="closeModal">×</button>
      </div>

      <!-- 검색 영역 -->
      <div class="search-section">
        <input
            type="text"
            v-model="searchKeyword"
            placeholder="활동 검색"
            class="search-input"
            @input="searchActivities"
        />
      </div>

      <!-- 활동 목록 -->
      <div class="activities-list" @scroll="handleScroll">
        <div
            v-for="activity in filteredActivities"
            :key="activity.actNo"
            class="activity-item"
            :class="{ 'selected': selectedActivity?.actNo === activity.actNo }"
            @click="selectActivity(activity)"
        >
          <div class="activity-info">
            <div class="activity-name">{{ activity.actName }}</div>
            <div class="activity-details">
              <span>{{ activity.actInten }} MET</span>
            </div>
          </div>
        </div>
        <div v-if="loading" class="loading">로딩 중...</div>
      </div>

      <!-- 선택된 활동 정보 -->
      <div v-if="selectedActivity" class="selected-section">
        <div class="selected-activity-info">
          <span class="selected-name">{{ selectedActivity.actName }}</span>
          <div class="duration-input">
            <label>활동 시간</label>
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

      <!-- 버튼 영역 -->
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
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import axiosInstance from '@/plugins/axios'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:isOpen', 'activity-added'])

const searchKeyword = ref('')
const selectedActivity = ref(null)
const duration = ref(1)
const searchResults = ref([])
const loading = ref(false)

const filteredActivities = computed(() => {
  if (!searchKeyword.value) return searchResults.value
  return searchResults.value.filter(activity =>
      activity.actName.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const canAdd = computed(() => {
  return selectedActivity.value && duration.value > 0 && duration.value <= 24
})

// 초기 활동 목록 로드
const loadInitialActivities = async () => {
  try {
    const { data } = await axiosInstance.get('/activity/list')
    searchResults.value = data
  } catch (error) {
    console.error('활동 목록 로드 실패:', error)
  }
}

// 검색어 변경 시 디바운스 처리
let searchTimeout
const searchActivities = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(async () => {
    try {
      const { data } = await axiosInstance.get('/activity/list', {
        params: { keyword: searchKeyword.value }
      })
      searchResults.value = data
    } catch (error) {
      console.error('활동 검색 실패:', error)
    }
  }, 300)
}

const selectActivity = (activity) => {
  selectedActivity.value = activity
  duration.value = 1
}

const addActivity = () => {
  if (canAdd.value) {
    const newActivity = {
      actNo: selectedActivity.value.actNo,
      actTime: duration.value,
      actName: selectedActivity.value.actName,
      actInten: selectedActivity.value.actInten
    }
    emit('activity-added', newActivity)
    closeModal()
  }
}

const closeModal = () => {
  emit('update:isOpen', false)
  selectedActivity.value = null
  duration.value = 1
  searchKeyword.value = ''
}

watch(() => props.isOpen, (newValue) => {
  if (newValue) {
    loadInitialActivities()
  }
})
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
  width: 95%;
  max-width: 500px;
  border-radius: 12px;
  padding: 20px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}

.search-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 16px;
}

.activities-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.activity-item.selected {
  background-color: #e3f2fd;
  border-left: 3px solid #2196f3;
}

.activity-info {
  flex: 1;
}

.activity-name {
  font-weight: bold;
  margin-bottom: 4px;
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
}

.selected-section {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.duration-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.duration-input input {
  width: 80px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
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

.loading {
  text-align: center;
  padding: 10px;
  color: #666;
}
</style>