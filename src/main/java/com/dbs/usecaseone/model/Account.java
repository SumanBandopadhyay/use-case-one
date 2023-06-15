package com.dbs.usecaseone.model;

public record Account(String accountNumber, String accountType, String currency, String panNbr, Boolean overdraft_ind) {
}
