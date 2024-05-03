package com.aroubeidis.cards.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Permission {

	ADMIN_READ("admin:read"),
	ADMIN_UPDATE("admin:update"),
	ADMIN_CREATE("admin:create"),
	ADMIN_DELETE("admin:delete"),
	MEMBER_READ("member:read"),
	MEMBER_UPDATE("member:update"),
	MEMBER_CREATE("member:create"),
	MEMBER_DELETE("member:delete");

	private final String value;
}