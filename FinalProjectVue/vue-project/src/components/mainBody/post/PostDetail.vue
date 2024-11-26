<template>
  <div class="detail-container">
    <h2>상세 게시물</h2>
    <div class="content-wrapper">
      <div class="title-row">
        <span class="title-value">{{ list.postTitle }}</span>
      </div>

      <div class="info-section">
        <div class="info-row">
          <span class="info-group">
            <span class="label">작성자</span>
            <span class="value">{{ list.nickname }}</span>
          </span>
          <span class="info-group">
            <span class="label">조회수</span>
            <span class="value">{{ list.postViews }}</span>
          </span>
        </div>

        <div class="info-row">
          <span class="info-group">
            <span class="label">등록일</span>
            <span class="value">{{ list.postCreatedDate }}</span>
          </span>
          <span class="info-group">
            <span class="label">수정일</span>
            <span class="value">{{ list.postUpdatedDate }}</span>
          </span>
        </div>
      </div>

      <div class="content-box">
        <div class="content-value">{{ list.postContent }}</div>
      </div>

      <div class="button-group">
        <template v-if="list.isWriter">
          <RouterLink class="btn btn-update" :to="{ name: 'postUpdate'}">수정</RouterLink>
          <RouterLink class="btn btn-delete" :to="{name: 'posts'}" @click="requestPostDelete">삭제</RouterLink>
        </template>
        <RouterLink class="btn btn-list" :to="{name: 'posts'}">목록</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
// 스크립트 부분은 동일하게 유지
import {ref, watch} from 'vue';
import {useRoute} from 'vue-router';
import axiosInstance from "@/plugins/axios.js";

const route = useRoute()
const currentMenu = ref('');
const currentView = ref('');
const postNo = ref(route.params.postNo)
const list = ref({});
const commentList = ref({});

const requestPostDetail = async () => {
  const {data} = await axiosInstance.get(`/post/post/${postNo.value}`);
  list.value = data;
}

const requestPostDelete = async () => {
  const isConfirmed = confirm('정말 삭제하시겠습니까?');

  if (isConfirmed) {
    try {
      await axiosInstance.delete(`/post/post/${postNo.value}`);
      window.location.reload();
    } catch (error) {
      console.error('삭제 실패:', error);
      alert('삭제에 실패했습니다.');
    }
  }
}

watch(() => route.path, async (path) => {
      const [_, menu, view, postId] = path.split('/');
      currentMenu.value = menu || '';
      currentView.value = view || '';
      postNo.value = postId || '';

      if (postNo.value) {
        await requestPostDetail(postNo.value);
      }
    },
    {immediate: true}
);
</script>

<style scoped>
.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h2 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 40px;
  font-size: 26px;
  font-weight: 600;
}

.content-wrapper {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 35px;
}

.title-row {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 20px;
}

.title-value {
  flex: 1;
  font-size: 25px;
  margin-left: 15px;
  padding: 8px 0;
}

.info-section {
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
}

.info-row {
  display: flex;
  gap: 30px;
  margin-bottom: 15px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-group {
  display: flex;
  align-items: center;
  flex: 1;
}

.label {
  background-color: #93c5fd;
  color: white;
  padding: 8px 8px;
  border-radius: 25px;
  font-weight: 600;
  font-size: 14px;
  min-width: 80px;
  text-align: center;
  margin-right: 15px;
  box-shadow: 0 2px 4px rgba(147, 197, 253, 0.2);
}

.value {
  flex: 1;
}

.content-box {
  margin-top: 30px;
}

.content-value {
  margin-top: 15px;
  padding: 30px;
  min-height: 200px;
  background: rgb(252, 252, 252);
  border-radius: 12px;
  line-height: 1.7;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 35px;
}

.btn {
  text-decoration: none;
  padding: 10px 3px;
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s ease;
  min-width: 90px;
  text-align: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.btn-update {
  background-color: #93c5fd;
  color: white;
}

.btn-delete {
  background-color: #ef4444;
  color: white;
}

.btn-list {
  background-color: #9ca3af;
  color: white;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  filter: brightness(105%);
}
</style>