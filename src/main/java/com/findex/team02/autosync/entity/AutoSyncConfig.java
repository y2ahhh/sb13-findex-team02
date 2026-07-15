package com.findex.team02.autosync.entity;

import com.findex.team02.global.entity.BaseEntity;
import com.findex.team02.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "auto_sync_config")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoSyncConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false)
    private IndexInfo indexInfo;

    @Column(nullable = false)
    private Boolean enabled;

    public AutoSyncConfig(IndexInfo indexInfo, Boolean enabled) {
        this.indexInfo = indexInfo;
        this.enabled = enabled;
    }

    public void updateEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
