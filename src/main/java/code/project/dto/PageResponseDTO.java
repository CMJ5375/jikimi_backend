package code.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResponseDTO<E> { //페이징처리

    //DTO목록
    private List<E> dtoList;

    // 페이징 UI에서 화면에 표시할 페이지 번호들을 미리 계산해 담아놓은 리스트
    //컬렉션에는 기본 자료형이 들어가지 못하므로 Integer자료형을 쓴다.
    private List<Integer> pageNumList;

    //검색조건, 현재페이지정보는 pageRequestDTO에 있다.
    private PageRequestDTO pageRequestDTO;

    //이전 다음 화살표
    private boolean prev, next;

    //총데이터, 이전페이지, 다음페이지, 총페이지, 현재페이지
    private int totalCount, prevPage, nextPage, totalPage, current;


    //페이지네이션 계산해서 넘겨주는 생성자
    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(List<E> dtoList, PageRequestDTO pageRequestDTO, long totalCount) {
        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = (int)totalCount;

        // 1 현재 페이지와 총 페이지 계산
        this.current = pageRequestDTO.getPage(); // 현재 페이지 (1부터 시작)
        this.totalPage = (int) Math.ceil(totalCount / (double) pageRequestDTO.getSize());

        // 2 블록 계산 (한 블록당 10페이지라고 가정)
        //끝값 = 올림(현재페이지 / 10.0) 이렇게하면 double이므로 int로 다운캐스팅  * 10
        int end = (int) (Math.ceil(this.current / 10.0)) * 10;
        //시작 값 = 끝값 - 9
        int start = end - 9;

        //마지막페이지 계산 : 총데이터(totalCount) / 10
        //만약 285이면 28.5 => 29
        // 총 페이지보다 블록의 end 값이 더 크면 마지막 페이지로 보정
        int last = this.totalPage;
        //마지막페이지이면 last
        end = end > last ? last : end;

        //3. 이전/다음 블록 존재 여부
        //이전, 1보다 크면 된다
        this.prev = start > 1;
        //다음, 만약 끝값이 39이고 한페이지당 10씩이면 39*10=390 => totalCount가 더크면
        this.next = totalCount > end * pageRequestDTO.getSize();
//        this.next = current < totalPage;

        //4. 페이지 번호 리스트  [1....10]
        this.pageNumList = IntStream.rangeClosed(start, end)
                .boxed()
                .collect(Collectors.toList());

        //5. 이전. 다음 블록의 첫 페이지 번호
        this.prevPage = prev ? start - 1 : 0;
        this.nextPage = next ? end + 1 : 0;

    }

}
