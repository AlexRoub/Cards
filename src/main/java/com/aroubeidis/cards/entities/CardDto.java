package com.aroubeidis.cards.entities;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import com.aroubeidis.cards.model.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CARDS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -4319487367611835303L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column
	private String description;

	@Column
	private String color;

	@Enumerated(EnumType.STRING)
	private Status status = Status.TO_DO;

	@Column(nullable = false)
	private LocalDate creationDate = LocalDate.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserDto user;
}
