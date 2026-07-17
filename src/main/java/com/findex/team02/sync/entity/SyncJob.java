package com.findex.team02.sync.entity;

import com.findex.team02.global.entity.BaseEntity;
import com.findex.team02.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_job")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SyncJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private IndexInfo indexInfo;

    private LocalDate targetDate;

    @Column(nullable = false)
    private String worker;

    @Column(nullable = false)
    private LocalDateTime jobTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncJobResult result;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncJobType jobType;

    @Builder
    private SyncJob(IndexInfo indexInfo, LocalDate targetDate, String worker,
                    LocalDateTime jobTime, SyncJobResult result, SyncJobType jobType) {
        this.indexInfo = indexInfo;
        this.targetDate = targetDate;
        this.worker = worker;
        this.jobTime = jobTime;
        this.result = result;
        this.jobType = jobType;
    }

}