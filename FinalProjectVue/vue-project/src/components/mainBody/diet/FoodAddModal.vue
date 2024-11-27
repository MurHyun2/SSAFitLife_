<template>
  <div v-if="isOpen" class="modal-overlay">
    <div class="modal-content">
      <!-- 탭 메뉴 -->
      <div class="tab-menu">
        <button
            :class="['tab-button', { active: activeTab === '식품 목록' }]"
            @click="activeTab = '식품 목록'"
        >
          식품 목록
        </button>
        <button
            :class="['tab-button', { active: activeTab === '식품 등록/수정' }]"
            @click="activeTab = '식품 등록/수정'"
        >
          식품 등록/수정
        </button>
        <button
            :class="['tab-button', { active: activeTab === '등록한 식품' }]"
            @click="activeTab = '등록한 식품'"
        >
          등록한 식품
        </button>
      </div>

      <!--  식품 목록 폼   -->
      <div v-if="activeTab === '식품 목록'">
        <!-- 검색 영역 -->
        <div class="search-section">
          <input
              type="text"
              v-model="searchKeyword"
              placeholder="음식 검색"
              class="search-input"
              @input="searchFood"
          />
        </div>

        <!-- 검색 결과 목록 -->
        <div class="search-results" @scroll="handleScroll">
          <div v-for="food in searchResults"
               :key="food.foodNo"
               class="food-item"
               :class="{ 'selected': selectedFood?.foodNo === food.foodNo }"
               @click="selectFood(food)"
          >
            <div class="food-info">
              <div class="food-name">{{ food.foodName }}</div>
              <div class="food-details">
                <span>{{food.foodAddCount}}회 기록</span><br>
                <span>{{ food.foodAmt }} g / {{ food.foodCalorie }} kcal</span>
              </div>
            </div>
            <div class="nutrition-info">
              <div class="nutrition-circle">
                <span>탄수화물</span>
                <span>{{ food.foodCarb }}g</span>
              </div>
              <div class="nutrition-circle">
                <span>단백질</span>
                <span>{{ food.foodProtein }}g</span>
              </div>
              <div class="nutrition-circle">
                <span>지방</span>
                <span>{{ food.foodFat }}g</span>
              </div>
            </div>
            <button class="add-button" @click="selectFood(food)">+</button>
          </div>
          <div v-if="loading" class="loading">로딩 중...</div>
        </div>

        <!-- 선택된 음식 수량 입력 -->
        <div v-if="selectedFood" class="quantity-section">
          <div class="selected-food-info">
            <span>{{ selectedFood.foodName }}</span>
            <div class="quantity-input">
              <input type="number" v-model="quantity" min="0.1" step="0.1"/>
              <span>인분</span>
            </div>
          </div>
        </div>

        <!-- 식사 구분 선택 -->
        <div v-if="selectedFood" class="meal-type-selection">
          <button
              v-for="type in mealTypes"
              :key="type.id"
              :class="['meal-type-btn', { active: selectedMealType === type.id }]"
              @click="selectedMealType = type.id"
          >
            {{ type.name }}
          </button>
        </div>

        <!-- 버튼 영역 -->
        <div class="modal-buttons">
          <button @click="closeModal" class="cancel-btn">취소</button>
          <button @click="addFood" class="confirm-btn" :disabled="!canAdd">추가하기</button>
        </div>
      </div>

      <!-- 식품 등록/수정 폼 -->
      <div v-if="activeTab === '식품 등록/수정'" class="food-register-form">
        <div class="form-group">
          <label>식품명</label>
          <input type="text" v-model="foodData.foodName" placeholder="식품명을 입력하세요"/>
        </div>
        <div class="form-group">
          <label>1회 제공량 (g)</label>
          <input type="number" v-model="foodData.foodAmt" placeholder="100"/>
        </div>
        <div class="nutrition-inputs">
          <div class="form-group">
            <label>칼로리 (kcal)</label>
            <input type="number" v-model="foodData.foodCalorie" placeholder="0"/>
          </div>
          <div class="form-group">
            <label>탄수화물 (g)</label>
            <input type="number" v-model="foodData.foodCarb" placeholder="0"/>
          </div>
          <div class="form-group">
            <label>단백질 (g)</label>
            <input type="number" v-model="foodData.foodProtein" placeholder="0"/>
          </div>
          <div class="form-group">
            <label>지방 (g)</label>
            <input type="number" v-model="foodData.foodFat" placeholder="0"/>
          </div>
        </div>
        <div class="modal-buttons">
          <button @click="closeModal" class="cancel-btn">취소</button>
          <button @click="registerFood" class="confirm-btn" :disabled="!isValidFood">
            {{ isEditMode ? '수정하기' : '등록하기' }}
          </button>
        </div>
      </div>

      <!-- 등록한 식품 폼 -->
      <div v-if="activeTab === '등록한 식품'">
        <div class="search-section">
          <input type="text" v-model="registeredSearchKeyword" placeholder="등록한 음식 검색" class="search-input" @input="searchRegisteredFood" />
        </div>
        <div class="search-results">
          <div v-for="food in registeredSearchResults" :key="food.foodNo" class="food-item" :class="{ 'selected': selectedFood?.foodNo === food.foodNo }" @click="selectFood(food)">
            <div class="food-info">
              <div class="food-name">{{ food.foodName }}</div>
              <div class="food-details">
                <span>{{food.foodAddCount}}회 기록</span><br>
                <span>{{ food.foodAmt }} g / {{ food.foodCalorie }} kcal</span>
              </div>
            </div>
            <div class="nutrition-info">
              <div class="nutrition-circle">
                <span>탄수화물</span>
                <span>{{ food.foodCarb }}g</span>
              </div>
              <div class="nutrition-circle">
                <span>단백질</span>
                <span>{{ food.foodProtein }}g</span>
              </div>
              <div class="nutrition-circle">
                <span>지방</span>
                <span>{{ food.foodFat }}g</span>
              </div>
            </div>
            <div class="action-buttons" @click.stop>
              <button class="add-button" @click="selectFood(food)">+</button>
              <button class="edit-button" @click="editFood(food)">수정</button>
              <button class="delete-button" @click="deleteFood(food)">삭제</button>
            </div>
          </div>
        </div>

        <!-- 선택된 음식 수량 입력 -->
        <div v-if="selectedFood" class="quantity-section">
          <div class="selected-food-info">
            <span>{{ selectedFood.foodName }}</span>
            <div class="quantity-input">
              <input type="number" v-model="quantity" min="0.1" step="0.1"/>
              <span>인분</span>
            </div>
          </div>
        </div>

        <!-- 식사 구분 선택 -->
        <div v-if="selectedFood" class="meal-type-selection">
          <button v-for="type in mealTypes" :key="type.id"
                  :class="['meal-type-btn', { active: selectedMealType === type.id }]"
                  @click="selectedMealType = type.id">
            {{ type.name }}
          </button>
        </div>

        <div class="modal-buttons">
          <button @click="closeModal" class="cancel-btn">취소</button>
          <button @click="addFood" class="confirm-btn" :disabled="!canAdd">추가하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, onMounted, watch} from 'vue';
import axiosInstance from '@/plugins/axios';
import { v4 as uuidv4 } from 'uuid';

const isOpen = ref(false);
const activeTab = ref('식품 목록');
const searchKeyword = ref('');
const allFoods = ref([]); // 전체 음식 데이터 저장
const searchResults = ref([]);
const selectedFood = ref(null);
const quantity = ref(1);
const selectedMealType = ref(null);
const page = ref(1);
const limit = 10;
const loading = ref(false);
const isEditMode = ref(false);
const editingFoodNo = ref(null);

const mealTypes = [
  {id: 0, name: '아침'},
  {id: 1, name: '점심'},
  {id: 2, name: '저녁'},
  {id: 3, name: '간식'}
];

// 모달 열기 시 자동으로 데이터 로드
watch(isOpen, async (newValue) => {
  if (newValue) {
    page.value = 1; // 페이지 초기화 추가
    await loadInitialData();
    await loadRegisteredFoods();
  }
});

// 초기 데이터 로드 함수
const loadInitialData = async () => {
  try {
    const response = await axiosInstance.get('/food');
    allFoods.value = response.data;
    searchResults.value = allFoods.value.slice(0, limit);
  } catch (error) {
    console.error('음식 데이터 로드 실패:', error);
  }
};
// 무한 스크롤 처리
const handleScroll = (e) => {
  const element = e.target;
  const {scrollTop, scrollHeight, clientHeight} = element;

  // 스크롤이 바닥에 도달했는지 확인 (여유값 10px 추가)
  if (scrollHeight - scrollTop <= clientHeight + 10) {
    loadMore();
  }
};

// 음식 선택 기능 추가
const selectFood = (food) => {
  selectedFood.value = food;
  quantity.value = 1; // 수량 초기화
};

const loadMore = () => {
  if (loading.value) return;
  loading.value = true;

  const nextPage = page.value + 1;
  const start = (nextPage - 1) * limit;
  const end = start + limit;

  // 검색어가 있는 경우 필터링된 결과에서 추가
  const currentResults = searchKeyword.value
      ? allFoods.value.filter(food =>
          food.foodName.toLowerCase().includes(searchKeyword.value.toLowerCase()))
      : allFoods.value;

  if (start < currentResults.length) {
    const newItems = currentResults.slice(start, end);
    searchResults.value = [...searchResults.value, ...newItems];
    page.value = nextPage;
  }

  loading.value = false;
};

const canAdd = computed(() => {
  return selectedFood.value && quantity.value > 0 && selectedMealType.value !== null;
});

// 검색 기능
const searchFood = () => {
  page.value = 1;
  if (searchKeyword.value) {
    const filtered = allFoods.value.filter(food =>
        food.foodName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    );
    searchResults.value = filtered.slice(0, limit);
  } else {
    searchResults.value = allFoods.value.slice(0, limit);
  }
};

const addFood = () => {
  const newDiet = {
    dietNo: uuidv4(), // 임시 dietNo 설정 -> 삭제처리 위해
    foodNo: selectedFood.value.foodNo,
    dietType: selectedMealType.value,
    foodQuantity: quantity.value,
    dietDate: new Date().toISOString().split('T')[0],
    food: selectedFood.value // 선택된 음식 정보 전체를 포함
  };

  emit('food-added', newDiet); // 새로운 식단 데이터를 부모 컴포넌트로 전달
  // closeModal();
};

const closeModal = () => {
  isOpen.value = false;
  selectedFood.value = null;
  quantity.value = 1;
  selectedMealType.value = null;
  searchKeyword.value = '';
  searchResults.value = [];
  page.value = 1;
  isEditMode.value = false;

  foodData.value = {
    foodName: '',
    foodAmt: 100,
    foodCalorie: 0,
    foodCarb: 0,
    foodProtein: 0,
    foodFat: 0,
    isShared: 1
  }
};

onMounted(() => {
  loadInitialData();
});

defineExpose({isOpen});
const emit = defineEmits(['food-added']);

// 식품 등록
const foodData = ref({
  foodName: '',
  foodAmt: 100,
  foodCalorie: 0,
  foodCarb: 0,
  foodProtein: 0,
  foodFat: 0,
  isShared: 1
})

const isValidFood = computed(() => {
  return foodData.value.foodName &&
      foodData.value.foodAmt > 0 &&
      foodData.value.foodCalorie >= 0
})

// registerFood 함수 수정
const registerFood = async () => {
  try {
    if (isEditMode.value) {
      await axiosInstance.put(`/food/${editingFoodNo.value}`, foodData.value);
      alert('식품이 수정되었습니다.');
    } else {
      await axiosInstance.post('/food', foodData.value);
      alert('식품이 등록되었습니다.');
    }

    // 초기화
    foodData.value = {
      foodName: '',
      foodAmt: 100,
      foodCalorie: 0,
      foodCarb: 0,
      foodProtein: 0,
      foodFat: 0,
      isShared: 1
    };
    isEditMode.value = false;
    editingFoodNo.value = null;

    await loadInitialData();
    await loadRegisteredFoods();
    activeTab.value = '등록한 식품';
  } catch (error) {
    console.error('식품 처리 실패:', error);
    alert(isEditMode.value ? '식품 수정에 실패했습니다.' : '식품 등록에 실패했습니다.');
  }
};

// 등록한 식품 관련 상태 추가
const registeredSearchKeyword = ref('');
const registeredSearchResults = ref([]);
const registeredFoods = ref([]);

// 등록한 식품 검색 기능
const searchRegisteredFood = () => {
  if (registeredSearchKeyword.value) {
    const filtered = registeredFoods.value.filter(food =>
        food.foodName.toLowerCase().includes(registeredSearchKeyword.value.toLowerCase())
    );
    registeredSearchResults.value = filtered;
  } else {
    registeredSearchResults.value = registeredFoods.value;
  }
}

// 식품 수정 함수
const editFood = (food) => {
  isEditMode.value = true;
  editingFoodNo.value = food.foodNo;
  foodData.value = {
    foodName: food.foodName,
    foodAmt: food.foodAmt,
    foodCalorie: food.foodCalorie,
    foodCarb: food.foodCarb,
    foodProtein: food.foodProtein,
    foodFat: food.foodFat,
    isShared: food.isShared
  };
  activeTab.value = '식품 등록/수정';
};

// 식품 삭제 함수
const deleteFood = async (food) => {
  if (confirm('정말 삭제하시겠습니까?')) {
    try {
      await axiosInstance.delete(`/food/${food.foodNo}`);
      await loadRegisteredFoods();
      selectedFood.value = null;
      alert('삭제가 완료되었습니다.')
    } catch (error) {
      console.error('식품 삭제 실패:', error);
    }
  }
};

// 등록한 식품 데이터 로드
const loadRegisteredFoods = async () => {
  try {
    const response = await axiosInstance.get('/food/registered');
    registeredFoods.value = response.data;
    registeredSearchResults.value = registeredFoods.value;
  } catch (error) {
    console.error('등록한 식품 로드 실패:', error);
  }
}

watch(activeTab, () => {
  selectedFood.value = null;
  quantity.value = 1;
  selectedMealType.value = null;
});
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
  max-width: 800px;
  border-radius: 12px;
  padding: 20px;
  max-height: 85vh;
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
  width: 100%;
  padding: 12px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 16px;
}

.food-item {
  display: grid;
  grid-template-columns: 1fr auto auto;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  gap: 20px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.food-item:hover {
  background-color: #f5f5f5;
}

.food-item.selected {
  background-color: #e3f2fd;
  border-left: 3px solid #2196f3;
}

.food-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.food-name {
  font-size: 16px;
  font-weight: bold;
}

.food-details {
  color: #666;
  font-size: 14px;
}

.nutrition-info {
  display: flex;
  gap: 10px;
}

.nutrition-circle {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #f8f9fa;
  padding: 10px;
  border-radius: 8px;
  min-width: 80px;
}

.nutrition-circle span:first-child {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.nutrition-circle span:last-child {
  font-weight: bold;
  color: #333;
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

.selected-food-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
}

.selected-food-info span {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.quantity-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-input input {
  width: 80px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  text-align: left; /* text-align을 left로 변경 */
  padding-left: 12px; /* 왼쪽 패딩 추가 */
}

.quantity-input span {
  color: #666;
  font-size: 14px;
}

.meal-type-selection {
  display: flex;
  gap: 10px;
  margin: 20px 0;
}

.meal-type-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: white;
  color: #666;
}

.meal-type-btn.active {
  background: #2196f3;
  color: white;
  border-color: #2196f3;
}


.loading {
  text-align: center;
  padding: 10px;
  color: #666;
}

.search-input {
  width: 96%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 20px;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.confirm-btn {
  background: #2196f3;
  color: white;
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.cancel-btn {
  background: #f0f0f0;
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.search-results {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
}

.food-register-form {
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
.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.add-button {
  background-color: #2196f3;
  color: white;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.add-button:hover {
  background-color: #1976d2;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(33, 150, 243, 0.2);
}

.edit-button {
  background-color: #f3f6fc;
  color: black;
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.edit-button:hover {
  background-color: #cccccc;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.delete-button {
  background-color: white;
  color: #f44336;
  padding: 8px 16px;
  border: 1px solid #f44336;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.delete-button:hover {
  background-color: #f44336;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(244, 67, 54, 0.2);
}

.quantity-section {
  margin-top: 20px;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.selected-food-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quantity-input {
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
}

.quantity-input input {
  width: 80px;
  font-size: 16px;
  color: #333;
  background-color: #fff;
  padding: 10px 12px;
  border: 2px solid transparent;
  border-radius: 8px;
  outline: none;
  transition: all 0.3s ease;
  background-image: linear-gradient(#fff, #fff), linear-gradient(120deg, #2196f3, #4CAF50);
  background-origin: border-box;
  background-clip: padding-box, border-box;
  box-shadow: 0 2px 4px rgba(33, 150, 243, 0.1);
}

.quantity-input input:focus {
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.2);
  transform: translateY(-1px);
}

.quantity-input input:hover {
  background-image: linear-gradient(#fff, #fff), linear-gradient(120deg, #4CAF50, #2196f3);
}

.quantity-input span {
  color: #666;
  font-size: 15px;
  font-weight: 500;
  padding-left: 4px;
}

/* 숫자 입력 화살표 스타일링 */
.quantity-input input::-webkit-inner-spin-button,
.quantity-input input::-webkit-outer-spin-button {
  opacity: 1;
  height: 24px;
  cursor: pointer;
}
</style>