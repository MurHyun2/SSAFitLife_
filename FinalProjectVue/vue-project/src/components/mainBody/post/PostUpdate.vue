<template>
  <div>
    <h2>게시물 수정</h2>
    <form @submit.prevent="updatePost">
      <div class="container">
        <span class="title">
          <span class="item">제목</span>
          <span class="body">
            <input class="update-title" type="text" v-model="list.postTitle" :placeholder="list.postTitle">
          </span>
        </span>
        <span class="create">
          <span class="item">등록일</span>
          <span class="body">{{ list.postCreatedDate }}</span>
        </span>
      </div>
      <div class="container">
        <span class="writer">
          <span class="item">작성자</span>
          <span class="body">{{ list.memNo }}</span>
        </span>
        <span class="update">
          <span class="item">수정일</span>
          <span class="body">{{ list.postUpdatedDate }}</span>
        </span>
      </div>
      <div class="container">
        <span class="views">
          <span class="item">조회수</span>
          <span class="body">{{ list.postViews }}</span>
        </span>
      </div>
      <div class="container">
        <span class="content">
          <span class="item">내용</span>
          <textarea class="update-content" v-model="list.postContent" :placeholder="list.postContent"></textarea>
        </span>
      </div>
      <div class="buttons">
        <button class="update-button" type="submit">수정</button>
        <RouterLink class="detail-button" :to="{ name: 'postDetail', params: { postNo: list.postNo } }">
          취소
        </RouterLink>
        <RouterLink class="post-button" :to="{ name: 'posts' }">목록</RouterLink>
      </div>
    </form>
  </div>
</template>

<script setup>
import {ref, watch, onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import axiosInstance from "@/plugins/axios.js";

const route = useRoute();
const router = useRouter();
const postNo = ref(route.params.postNo);
const list = ref({});

const requestPostDetail = async () => {
  const {data} = await axiosInstance.get(`http://localhost:8080/post/post/${postNo.value}`);
  list.value = data;
};

const updatePost = async () => {
  try {
    const {data} = await axiosInstance.put(`http://localhost:8080/post/post/${postNo.value}`, {
      postTitle: list.value.postTitle,
      postContent: list.value.postContent,
    });
    alert('게시물이 성공적으로 수정되었습니다.');
    router.push({name: 'postDetail', params: {postNo: postNo.value}}); // 수정 후 게시물 상세 페이지로 이동
  } catch (error) {
    console.error('게시물 수정에 실패했습니다:', error);
  }
};

onMounted(() => {
  requestPostDetail();
});

watch(() => route.params.postNo, (newPostNo) => {
  postNo.value = newPostNo;
  requestPostDetail();
}, {immediate: true});
</script>

<style scoped>
.buttons {
  margin-top: 20px;
  display: flex;
  margin-left: auto;
  margin-right: 30%;
  justify-content: flex-end;
}

.update-button, .detail-button, .post-button {
  text-decoration: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 1px 1px 5px rgba(0, 0, 0, 0.3);
  color: white;
  background-color: #97d4e9;
  border-radius: 35px;
  width: 64px;
  height: 27px;
  margin: 10px;
}

.update-button {
  color: blue;
}

.detail-button {
  color: black;
}

.post-button {
  color: gray;
}

.container {
  position: relative;
  display: flex;
  align-items: center;
}

.container span {
  margin: 8px;
  display: flex;
}

.item {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
  background-color: #97d4e9;
  border-radius: 35px;
  width: 64px;
  height: 27px;
}

.title {
  flex: 1;
}

.writer {
  flex: 1;
}

.create {
  flex: 1;
}

.update {
  flex: 1;
}

.body {
  display: flex;
  flex-shrink: 0;
}

.update-content {
  width: 50vw;
  height: 50vh;
}

h2 {
  display: flex;
  justify-content: center;
  margin-bottom: 60px;
}
</style>
