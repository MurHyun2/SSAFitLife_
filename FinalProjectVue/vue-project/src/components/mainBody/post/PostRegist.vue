<template>
  <div>
    <h2>게시물 등록</h2>
    <form @submit.prevent="registPost">
      <div class="container">
        <span class="title">
          <span class="item">제목</span>
          <span class="body">
            <input class="update-title" type="text" v-model="newPostTitle" :placeholder="'제목을 입력하세요.'">
          </span>
        </span>
      </div>
      <div class="container">
        <span class="content">
          <span class="item">내용</span>
          <textarea class="update-content" v-model="newPostContent" :placeholder="'내용을 입력하세요.'"></textarea>
        </span>
      </div>
      <div class="buttons">
        <button class="regist-button" @click.prevent="registPost">
          등록
        </button>
        <RouterLink class="post-button" :to="{ name: 'posts' }">목록</RouterLink>
      </div>
    </form>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import axiosInstance from "@/plugins/axios.js";

const router = useRouter();
const newPostTitle = ref('');
const newPostContent = ref('');
const userNo = ref('1');

const registPost = async () => {
  try {
    await axiosInstance.post(`http://localhost:8080/post/post/${userNo.value}`, {
      postTitle: newPostTitle.value,
      postContent: newPostContent.value,
    });
    alert('게시물이 성공적으로 등록되었습니다.');

    await router.push({name: 'posts'});
  } catch (error) {
    console.error('게시물 등록 실패:', error);
  }
};
</script>

<style scoped>
.buttons {
  margin-top: 20px;
  display: flex;
  margin-left: auto;
  margin-right: 30%;
  justify-content: flex-end;
}

.regist-button, .post-button {
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

.regist-button {
  color: blue;
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
