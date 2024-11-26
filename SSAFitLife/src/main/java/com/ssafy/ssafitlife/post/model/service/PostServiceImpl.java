package com.ssafy.ssafitlife.post.model.service;

import com.ssafy.ssafitlife.post.model.dao.PostDao;
import com.ssafy.ssafitlife.post.model.dto.PageResponse;
import com.ssafy.ssafitlife.post.model.dto.Post;
import com.ssafy.ssafitlife.post.model.dto.SearchCondition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class PostServiceImpl implements PostService {

	private final PostDao postDao;
	
	public PostServiceImpl(PostDao postDao) {
		this.postDao = postDao;
	}
	
	// 게시글 전체조회
	@Override
	public PageResponse<Post> getPostList(SearchCondition condition) {
		List<Post> posts = postDao.selectAll(condition);
		long total = postDao.getTotalCount();
		int totalPages = (int) Math.ceil((double) total / condition.getSize());

		return new PageResponse<>(posts, condition.getPage(), totalPages, total, condition.getSize());
	}

	// 게시글 상세조회
	@Override
	public Post readPost(int postNo) {
		postDao.updateViews(postNo);
		return postDao.selectOne(postNo);
	}

	// 게시글 등록
	@Transactional
	@Override
	public boolean writePost(Post Post) {
		return postDao.insertPost(Post)==1;
	}
	
	// 게시글 삭제
	@Transactional
	@Override
	public boolean removePost(int postNo) {
		postDao.deleteComment(postNo);
		return postDao.deletePost(postNo) == 1;
	}

	// 게시글 수정
	@Transactional
	@Override
	public boolean modifyPost(Post Post) {
		return postDao.updatePost(Post)==1;
	}

	// 게시글 검색
	@Override
	public PageResponse<Post> search(SearchCondition condition) {
		List<Post> posts = postDao.search(condition);
		long total = postDao.getSearchCount(condition);
		int totalPages = (int) Math.ceil((double) total / condition.getSize());

		return new PageResponse<>(posts, condition.getPage(), totalPages, total, condition.getSize());
	}
}
