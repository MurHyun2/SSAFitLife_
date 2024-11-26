<template>
  <div class="list-container">
    <div class="board-header">
      <div class="header-row">
        <span class="header-title">제목</span>
        <span class="header-content">내용</span>
        <span class="header-writer">작성자</span>
        <span class="header-create">작성일</span>
        <span class="header-views">조회수</span>
      </div>
    </div>

    <div class="board-body">
      <div v-for="list in lists" :key="list.postNo">
        <RouterLink :to="{ name: 'postDetail', params: { postNo: list.postNo } }" custom>
          <template #default="{ navigate }">
            <div class="post-row" @click="navigate">
              <span class="post-title">{{ list.postTitle }}</span>
              <span class="post-content">
                {{ list.postContent.length > 25 ? list.postContent.slice(0, 25) + ' ...' : list.postContent }}
              </span>
              <span class="post-writer">{{ list.nickname }}</span>
              <span class="post-create">{{ list.postCreatedDate }}</span>
              <span class="post-views">{{ list.postViews }}</span>
            </div>
          </template>
        </RouterLink>
      </div>
    </div>
  </div>

  <div class="pagination">
    <button
        class="page-btn"
        :disabled="currentPage === 1"
        @click="changePage(currentPage - 1)"
    >
      이전
    </button>

    <span v-for="pageNum in totalPages" :key="pageNum">
      <button
          class="page-btn"
          :class="{ active: pageNum === currentPage }"
          @click="changePage(pageNum)"
      >
        {{ pageNum }}
      </button>
    </span>

    <button
        class="page-btn"
        :disabled="currentPage === totalPages"
        @click="changePage(currentPage + 1)"
    >
      다음
    </button>
  </div>

  <div class="board-footer">
    <div class="button-container">
      <RouterLink class="regist-button" :to="{ name: 'postRegist'}">등록</RouterLink>
    </div>

    <div class="search-container">
      <select v-model="searchStandard" class="search-select">
        <option>제목</option>
        <option>내용</option>
        <option>작성자</option>
      </select>
      <input
          type="text"
          class="search-input"
          v-model="searchKeyword"
          :placeholder="`검색할 ${searchStandard} 입력`"
          @keyup.enter="searchPosts"
      />
      <button class="search-button" @click="searchPosts">검색</button>
      <button class="reset-button" @click="resetSearch">초기화</button>
    </div>
  </div>
</template>

<script setup>
import {ref, watch} from 'vue';
import {useRoute} from 'vue-router';
import axiosInstance from "@/plugins/axios.js";

const currentMenu = ref('');
const currentView = ref('');
const route = useRoute();
const lists = ref([]);
const searchStandard = ref('제목');
const searchKeyword = ref(''); // 검색어를 저장할 ref 추가
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = ref(10);

const requestPostList = async () => {
  try {
    const { data } = await axiosInstance.get('/post/post', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    });
    lists.value = data.content;
    totalPages.value = data.totalPages;
  } catch (error) {
    console.error('게시물 리스트를 불러오는데 실패했습니다:', error);
  }
};

const changePage = async (page) => {
  currentPage.value = page;
  if (searchKeyword.value) {
    await searchPosts();
  } else {
    await requestPostList();
  }
};

const searchPosts = async () => {
  try {
    const params = {
      searchType: searchStandard.value,
      keyword: searchKeyword.value,
      page: currentPage.value,
      size: pageSize.value
    };

    const { data } = await axiosInstance.get('/post/search', { params });
    lists.value = data.content;
    totalPages.value = data.totalPages;
  } catch (error) {
    console.error('검색 중 오류가 발생했습니다:', error);
  }
};

// 검색어 초기화 함수 추가
const resetSearch = () => {
  searchKeyword.value = '';
  requestPostList();
};

watch(() => route.path, async (path) => {
  const [_, menu, view] = path.split('/');
  currentMenu.value = menu || '';
  currentView.value = view || '';
  if (menu === 'board') {
    await requestPostList();
  }
}, { immediate: true });

</script>

<style scoped>
<style scoped>
.list-container {
  max-width: 1200px;
  margin: 20px auto;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.board-header {
  background-color: #93c5fd;
  padding: 16px 0;
  color: white;
}

.header-row, .post-row {
  display: flex;
  align-items: center;
  padding: 0 24px;
}

.header-row span {
  font-weight: 600;
  font-size: 15px;
  color: white;
}

.post-row {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.post-row:hover {
  background-color: #f8fafc;
  transform: translateX(4px);
}

.header-title, .post-title { flex: 2; }
.header-content, .post-content {
  flex: 3;
}
.header-writer, .post-writer {
  flex: 1;
}
.header-create, .post-create {
  flex: 1.5;
  font-size: 14px;
}
.header-views, .post-views {
  flex: 1;
}

.board-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin: 30px 0;
}

.page-btn {
  padding: 8px 16px;
  border: none;
  background-color: white;
  color: #6b7280;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
  min-width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-btn:hover:not(:disabled) {
  background-color: #93c5fd;
  color: white;
  transform: translateY(-2px);
}

.page-btn.active {
  background-color: #93c5fd;
  color: white;
  box-shadow: 0 2px 8px rgba(147, 197, 253, 0.3);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.regist-button {
  text-decoration: none;
  padding: 10px 24px;
  background-color: #93c5fd;
  color: white;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(147, 197, 253, 0.2);
}

.regist-button:hover {
  background-color: #60a5fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(147, 197, 253, 0.3);
}

.search-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-select {
  padding: 10px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background-color: white;
  color: #4b5563;
  font-size: 14px;
  transition: all 0.2s ease;
}

.search-select:focus {
  border-color: #93c5fd;
  outline: none;
  box-shadow: 0 0 0 3px rgba(147, 197, 253, 0.1);
}

.search-input {
  padding: 10px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  width: 240px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.search-input:focus {
  border-color: #93c5fd;
  outline: none;
  box-shadow: 0 0 0 3px rgba(147, 197, 253, 0.1);
}

.search-button, .reset-button {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
  font-size: 14px;
}

.search-button {
  background-color: #93c5fd;
  color: white;
}

.search-button:hover {
  background-color: #60a5fa;
  transform: translateY(-2px);
}

.reset-button {
  background-color: #f87171;
  color: white;
}

.reset-button:hover {
  background-color: #ef4444;
  transform: translateY(-2px);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .header-content, .post-content {
    display: none;
  }

  .search-container {
    flex-wrap: wrap;
  }

  .search-input {
    width: 100%;
  }
}
</style>