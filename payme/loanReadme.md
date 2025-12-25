# Loan Management System - Database & Logic Design

This document provides a comprehensive overview of the Loan Management System designed for the `Payme` backend.

## 1. System Overview
The Loan System provides automated micro-loans with an internal scoring fallback mechanism.

## 2. Database Schema

### A. LoanProduct
Defines terms for loan types.
- `interest_rate_monthly`: **2.5%** (Reducing balance).
- `penalty_rate_daily`: **1.5%** (Daily).

### B. LoanApplication
Captures user requests and credit evaluation.
- `max_eligible_amount`: Credit limit from scoring.
- `requested_amount`: User-selected amount.

### C. Loan
The active contract after disbursement.
- `remaining_balance`: Current outstanding amount.

### D. RepaymentSchedule
Monthly installment breakdown.
- `principal_due`, `interest_due`, `total_due`.

### E. LoanTransaction
Records of payments linked to Wallet transactions.

## 3. Business Rules
- **Interest**: 2.5% monthly on reducing balance.
- **Penalties**: 1.5% daily for overdue amounts.
- **Early Repayment**: No interest discounts for early closure.

## 4. Scoring Logic (Fallback Strategy)
1. **External**: Credit Bureau API.
2. **Internal**: Based on Transaction Volume, Wallet Balance, and Account Age.

## 5. Operational Workflow
1. **Application** -> **Scoring** -> **Limit Calculation**.
2. **User Input** -> **Auto-Approval** -> **Disbursement**.
3. **Repayment** -> **Balance Update**.
