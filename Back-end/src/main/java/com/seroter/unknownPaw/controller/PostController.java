import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Log4j2
public class PostController {

  private final PetOwnerService petOwnerService;
  private final PetSitterService petSitterService;

  @Value("${com.example.upload.path}")
  private String uploadPath;


  //  apiserver 기준으로 controller 작성후 쳇지피티 대조후 요약해준 코드로 구성하였습니다.
  //  petOwner 와 petSitter 을  entity 에서 userRole 구성후 기준으로 코드를 구성하였습니다.
  //  pageRquestDTO와 pageResultDTO 를 사용한다는 가정하에 코드 구성하였습니다.


  // post 목록 조회
  @GetMapping("/{role}/list")
  public ResponseEntity<?> list(@PathVariable String role, PageRequestDTO pageRequestDTO) {
    if ("petOwner".equals(role)) {
      return ResponseEntity.ok(petOwnerService.getList(pageRequestDTO));
    } else if ("petSitter".equals(role)) {
      return ResponseEntity.ok(petSitterService.getList(pageRequestDTO));
    }
    return ResponseEntity.badRequest().body("Invalid role");
  }

  // post 상세 조회
  @GetMapping("/{role}/read/{postId}")
  public ResponseEntity<?> read(@PathVariable String role, @PathVariable Long postId) {
    PostDTO postDTO = "petOwner".equals(role) ? petOwnerService.get(postId) : petSitterService.get(postId);
    return ResponseEntity.ok(postDTO);
  }

  // post 등록
  @PostMapping("/{role}/register")
  public ResponseEntity<?> register(@PathVariable String role, @RequestBody PostDTO postDTO) {
    Long newId = "petOwner".equals(role) ? petOwnerService.register(postDTO) : petSitterService.register(postDTO);
    return ResponseEntity.ok(Map.of("postId", newId));
  }

  // post 수정
  @PutMapping("/{role}/modify")
  public ResponseEntity<?> modify(@PathVariable String role, @RequestBody ModifyRequestDTO modifyRequestDTO) {
    PostDTO dto = modifyRequestDTO.getPostDTO();
    if ("petOwner".equals(role)) {
      petOwnerService.modify(dto);
    } else {
      petSitterService.modify(dto);
    }
    return ResponseEntity.ok(Map.of("msg", "수정 완료", "postId", dto.getPostId()));
  }

  // post 삭제
  @DeleteMapping("/{role}/delete/{postId}")
  public ResponseEntity<?> delete(@PathVariable String role, @PathVariable Long postId) {
    if ("petOwner".equals(role)) {
      petOwnerService.remove(postId);
    } else {
      petSitterService.remove(postId);
    }
    return ResponseEntity.ok(Map.of("msg", "삭제 완료", "postId", postId));
  }


  // post 이미지 저장
  @PostMapping("/upload/{role}")
  public ResponseEntity<?> upload(@PathVariable String role, @RequestParam("file") MultipartFile file) {
    try {
      if (!role.equals("petOwner") && !role.equals("petSitter")) {
        return ResponseEntity.badRequest().body("올바르지 않은 역할(role)입니다.");
      }

      String originalName = file.getOriginalFilename();
      String saveName = UUID.randomUUID() + "_" + originalName;

      File roleDir = new File(uploadPath, role);
      if (!roleDir.exists()) {
        roleDir.mkdirs(); // 디렉토리 없으면 생성
      }

      File saveFile = new File(roleDir, saveName);
      file.transferTo(saveFile);

      return ResponseEntity.ok(Map.of("fileName", saveName, "role", role));
    } catch (Exception e) {
      log.error("파일 업로드 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
    }
  }

  // post 이미지 수정 (기존 파일 삭제 후 새 파일 저장)
  @PostMapping("/replace/{role}")
  public ResponseEntity<?> replaceImage(
      @PathVariable String role,
      @RequestParam("oldFileName") String oldFileName,
      @RequestParam("file") MultipartFile newFile) {
    try {
      File oldFile = new File(uploadPath + "/" + role, oldFileName);
      if (oldFile.exists()) oldFile.delete();

      String originalName = newFile.getOriginalFilename();
      String saveName = UUID.randomUUID() + "_" + originalName;

      File roleDir = new File(uploadPath, role);
      if (!roleDir.exists()) roleDir.mkdirs();

      File saveFile = new File(roleDir, saveName);
      newFile.transferTo(saveFile);

      return ResponseEntity.ok(Map.of("fileName", saveName, "message", "이미지 교체 성공"));
    } catch (Exception e) {
      log.error("이미지 교체 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 교체 실패");
    }
  }

  // post 이미지 삭제
  @DeleteMapping("/{role}/{fileName}")
  public ResponseEntity<?> deleteImage(@PathVariable String role, @PathVariable String fileName) {
    try {
      File file = new File(uploadPath + "/" + role, fileName);
      if (file.exists()) {
        file.delete();
        return ResponseEntity.ok("삭제 성공");
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일이 존재하지 않습니다.");
      }
    } catch (Exception e) {
      log.error("이미지 삭제 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
    }
  }
}
