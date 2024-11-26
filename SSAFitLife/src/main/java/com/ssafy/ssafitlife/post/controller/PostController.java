package com.ssafy.ssafitlife.post.controller;


import com.ssafy.ssafitlife.post.model.dto.PageResponse;
import com.ssafy.ssafitlife.post.model.dto.Post;
import com.ssafy.ssafitlife.post.model.dto.SearchCondition;
import com.ssafy.ssafitlife.post.model.service.PostService;
import com.ssafy.ssafitlife.security.model.dto.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController // @Controller + @ResponseBody
@RequestMapping("/post")
public class PostController {
	// 서비스 의존성 주입
	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	// 게시글 전체조회

	@GetMapping("/post")
	public ResponseEntity<Object> list(@RequestParam(defaultValue = "1") int page,
									   @RequestParam(defaultValue = "10") int size) {
		try {
			SearchCondition condition = new SearchCondition();
			condition.setPage(page);
			condition.setSize(size);

			PageResponse<Post> pageResponse = postService.getPostList(condition);
			return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("서버 오류로 인해 게시글 불러오기에 실패했습니다.");
		}
	}

	@GetMapping("/search")
	public ResponseEntity<Object> search(@RequestParam String searchType,
										 @RequestParam String keyword,
										 @RequestParam(defaultValue = "1") int page,
										 @RequestParam(defaultValue = "10") int size) {
		try {
			SearchCondition condition = new SearchCondition();
			condition.setPage(page);
			condition.setSize(size);

			switch(searchType) {
				case "제목":
					condition.setSearchType("post_title");
					break;
				case "내용":
					condition.setSearchType("post_content");
					break;
				case "작성자":
					condition.setSearchType("nickname");
					break;
				default:
					condition.setSearchType("post_title");
			}

			condition.setKeyword(keyword);
			PageResponse<Post> pageResponse = postService.search(condition);
			return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("검색 중 오류가 발생했습니다.");
		}
	}

	// 게시글 상세보기
	@GetMapping("/post/{postNo}")
	public ResponseEntity<Object> detail(@PathVariable("postNo") int postNo, @AuthenticationPrincipal CustomUserDetails user) {
		System.out.println("게시글상세보기");
		try {
			Post post = postService.readPost(postNo);

            post.setIsWriter(post.getMemNo() == user.getMemNo());

			if (post != null) {
				return ResponseEntity.status(HttpStatus.OK).body(post);
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 삭제되었거나 존재하지 않습니다");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류로 인해 게시글 불러오기에 실패했습니다.");
		}
	}

	// 게시글 등록
	@PostMapping("/post")
	public ResponseEntity<String> write(@RequestBody Post post, @AuthenticationPrincipal CustomUserDetails user) {
		post.setMemNo(user.getMemNo());
		
		if (postService.writePost(post))
			return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 등록되었습니다.");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 등록에 실패했습니다.");
	}

	// 게시글 삭제
	@DeleteMapping("/post/{postNo}")
	public ResponseEntity<String> delete(@PathVariable("postNo") int postNo) {
		boolean isDeleted = postService.removePost(postNo);

		if (isDeleted)
			return ResponseEntity.status(HttpStatus.OK).body("게시글이 삭제되었습니다.");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제에 실패했습니다.");
	}

	// 게시글 수정
//	@PutMapping("/post")
	@PutMapping("/post/{postNo}")
	public ResponseEntity<String> update(@PathVariable("postNo") int postNo, @RequestBody Post post) {

		post.setPostNo(postNo);
		if (postService.modifyPost(post))
			return ResponseEntity.status(HttpStatus.OK).body("게시글이 수정되었습니다.");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정에 실패했습니다.");
	}
}
