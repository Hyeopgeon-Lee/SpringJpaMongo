package kopo.poly.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.repository.NoticeRepository;
import kopo.poly.repository.entity.NoticeEntity;
import kopo.poly.service.INoticeService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("NoticeService")
public class NoticeService implements INoticeService {

    // RequiredArgsConstructor 어노테이션으로 생성자를 자동 생성함
    // noticeRepository 변수에 이미 메모리에 올라간 NoticeRepository 객체를 넣어줌
    // 예전에는 autowired 어노테이션를 통해 설정했었지만, 이젠 생성자를 통해 객체 주입함
    private final NoticeRepository noticeRepository;

    @Override
    public List<NoticeDTO> getNoticeList() {

        log.info(this.getClass().getName() + ".getNoticeList Start!");

        // 공지사항 전체 리스트 조회하기
        List<NoticeEntity> rList = noticeRepository.findAllByOrderByNoticeSeqDesc();

        // 엔티티의 값들을 DTO에 맞게 넣어주기
        List<NoticeDTO> nList = new ObjectMapper().convertValue(rList,
                new TypeReference<List<NoticeDTO>>() {
                });

        log.info(this.getClass().getName() + ".getNoticeList End!");

        return nList;
    }

    @Transactional
    @Override
    public NoticeDTO getNoticeInfo(NoticeDTO pDTO, boolean type) {

        log.info(this.getClass().getName() + ".getNoticeInfo Start!");

        // 공지사항 상세내역 가져오기
        NoticeEntity rEntity = noticeRepository.findByNoticeSeq(pDTO.getNoticeSeq());

        if (type) {

            // 조회수 증가하기
            // MongoDB는 @DynamicUpdate를 아직 지원하지 않아, 기존 값을 불러와 값을 저장하고, 수정할 값을 넣어야 함
            NoticeEntity pEntity = NoticeEntity.builder()
                    .noticeSeq(rEntity.getNoticeSeq()).title(rEntity.getTitle())
                    .noticeYn(rEntity.getNoticeYn()).contents(rEntity.getContents())
                    .userId(rEntity.getUserId())
                    .readCnt(rEntity.getReadCnt() + 1)
                    .regId(rEntity.getRegId()).regDt(rEntity.getRegDt())
                    .chgId(rEntity.getChgId()).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .build();

            noticeRepository.save(pEntity);

            // 조회수 증가 후, 다시 조회하기
            // 다시 조회하지 않으면, 조회수가 증가되지 않은 이전 값들이 전달됨
            rEntity = noticeRepository.findByNoticeSeq(pDTO.getNoticeSeq());

        }

        // 엔티티의 값들을 DTO에 맞게 넣어주기
        NoticeDTO rDTO = new ObjectMapper().convertValue(rEntity, NoticeDTO.class);

        log.info(this.getClass().getName() + ".getNoticeInfo End!");

        return rDTO;
    }

    @Transactional
    @Override
    public void updateNoticeInfo(NoticeDTO pDTO) {

        log.info(this.getClass().getName() + ".updateNoticeInfo Start!");

        String noticeSeq = CmmUtil.nvl(pDTO.getNoticeSeq());
        String title = CmmUtil.nvl(pDTO.getTitle());
        String noticeYn = CmmUtil.nvl(pDTO.getNoticeYn());
        String contents = CmmUtil.nvl(pDTO.getContents());
        String userId = CmmUtil.nvl(pDTO.getUserId());

        log.info("noticeSeq : " + noticeSeq);
        log.info("title : " + title);
        log.info("noticeYn : " + noticeYn);
        log.info("contents : " + contents);
        log.info("userId : " + userId);

        // 공지사항 상세내역 가져오기
        NoticeEntity rEntity = noticeRepository.findByNoticeSeq(pDTO.getNoticeSeq());

        // 수정할 값들을 빌더를 통해 엔티티에 저장하기
        // MongoDB는 @DynamicUpdate를 아직 지원하지 않아, 기존 값을 불러와 값을 저장하고, 수정할 값을 넣어야 함
        NoticeEntity pEntity = NoticeEntity.builder()
                .noticeSeq(noticeSeq).title(title)
                .noticeYn(noticeYn).contents(contents)
                .userId(userId)
                .readCnt(rEntity.getReadCnt())
                .regId(rEntity.getRegId()).regDt(rEntity.getRegDt())
                .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .build();

        // 데이터 수정하기
        noticeRepository.save(pEntity);

        log.info(this.getClass().getName() + ".updateNoticeInfo End!");

    }

    @Transactional
    @Override
    public void deleteNoticeInfo(NoticeDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteNoticeInfo Start!");

        String noticeSeq = CmmUtil.nvl(pDTO.getNoticeSeq());
        log.info("noticeSeq : " + noticeSeq);

        // 데이터 수정하기
        noticeRepository.deleteById(noticeSeq);


        log.info(this.getClass().getName() + ".deleteNoticeInfo End!");
    }

    @Transactional
    @Override
    public void InsertNoticeInfo(NoticeDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".InsertNoticeInfo Start!");

        String title = CmmUtil.nvl(pDTO.getTitle());
        String noticeYn = CmmUtil.nvl(pDTO.getNoticeYn());
        String contents = CmmUtil.nvl(pDTO.getContents());
        String userId = CmmUtil.nvl(pDTO.getUserId());

        log.info("title : " + title);
        log.info("noticeYn : " + noticeYn);
        log.info("contents : " + contents);
        log.info("userId : " + userId);

        // 공지사항 저장을 위해서는 PK 값은 빌더에 추가하지 않는다.
        // JPA에 자동 증가 설정을 해놨음
        NoticeEntity pEntity = NoticeEntity.builder()
                .title(title).noticeYn(noticeYn).contents(contents).userId(userId).readCnt(0L)
                .regId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .build();

        // 공지사항 저장하기
        noticeRepository.save(pEntity);

        log.info(this.getClass().getName() + ".InsertNoticeInfo End!");

    }
}
