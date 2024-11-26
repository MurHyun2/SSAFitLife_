package com.ssafy.ssafitlife.post.model.service;


import com.ssafy.ssafitlife.post.model.dto.PageResponse;
import com.ssafy.ssafitlife.post.model.dto.Post;
import com.ssafy.ssafitlife.post.model.dto.SearchCondition;

import java.util.List;

public interface PostService {
	// 게시글 전체 조회
	PageResponse<Post> getPostList(SearchCondition condition);

	// 게시글 상세조회 (클릭시 읽는거)
	Post readPost(int postNo);

	// 게시글 작성
	boolean writePost(Post Post);

	// 게시글 삭제
	boolean removePost(int postNo);

	// 게시글 수정
	boolean modifyPost(Post Post);

	// 검색버튼을 눌러을때 처리할 메서드
	PageResponse<Post> search(SearchCondition condition);
}
