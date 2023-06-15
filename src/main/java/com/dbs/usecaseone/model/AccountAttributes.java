package com.dbs.usecaseone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountAttributes {
    private String accountNumber;
    private String panNbr;
    private Boolean overdraft_ind;
}
