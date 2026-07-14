package com.findex.team02.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass   // 직접 테이블을 만들지 않고, 자식 엔티티의 테이블에 컬럼으로 포함된다
@EntityListeners(AuditingEntityListener.class)  // JPA Auditing 이벤트 리스너 등록
public abstract class BaseEntity {
  @CreatedDate                  // 엔티티가 처음 저장될 때 자동으로 현재 시각이 입력된다
  @Column(name = "create_at", nullable = false, updatable = false)    // 최초 생성 이후 수정되면 안 되므로 updatable = false 설정
  private LocalDateTime createdAt;
  @LastModifiedDate             // 엔티티가 수정될 때마다 자동으로 현재 시각이 갱신된다
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
