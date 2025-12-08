# Payme API Documentation

## Authentication

### 1. Register User
**Endpoint:** `POST /api/v1/auth/register`

**Description:** Registers a new user with the system.

**Request Body:**
```json
{
  "name": "John Doe",          // Required, 2-100 characters
  "email": "john@example.com", // Required, valid email format
  "phone": "99112233",         // Required, exactly 8 digits
  "password": "password123"    // Required, min 6 characters
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "99112233",
      "createdAt": "2023-10-27T10:00:00"
    }
  },
  "timestamp": "2023-10-27T10:00:00.123456"
}
```

---

### 2. Login User
**Endpoint:** `POST /api/v1/auth/login`

**Description:** Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "email": "john@example.com", // Required
  "password": "password123"    // Required
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "99112233",
      "createdAt": "2023-10-27T10:00:00"
    }
  },
  "timestamp": "2023-10-27T10:05:00.123456"
}
```

**Error Response (Example):**
```json
{
  "success": false, 
  "message": "Invalid credentials",
  "errorCode": "AUTHENTICATION_FAILED",
  "data": null,
  "timestamp": "2023-10-27T10:05:00.123456"
}
```

---

## Users

### 1. Get Current User (Me)
**Endpoint:** `GET /api/v1/users/me`

**Description:** Retrieves the currently authenticated user's profile information based on the JWT token.

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "99112233",
    "createdAt": "2023-10-27T10:00:00"
  },
  "timestamp": "2023-10-27T10:05:00.123456"
}
```

### 2. Update User Profile
**Endpoint:** `PUT /api/v1/users/update`

**Description:** Updates the currently authenticated user's profile information.

**Request Body:**
```json
{
  "name": "John Updated",       // Optional, 2-100 characters
  "email": "john.new@example.com", // Optional, valid email
  "phone": "88112233",          // Optional, exactly 8 digits
  "address": "New Address St."  // Optional, max 500 chars
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "User updated successfully",
  "data": {
    "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
    "name": "John Updated",
    "email": "john.new@example.com",
    "phone": "88112233",
    "createdAt": "2023-10-27T10:00:00"
  },
  "timestamp": "2023-10-27T10:10:00.123456"
}
```

### 3. Search Users
**Endpoint:** `GET /api/v1/users/search`

**Description:** Search for users by name or phone number.

**Query Parameters:**
*   `query`: Search term (name or phone number).

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Search completed successfully",
  "data": [
    {
      "userId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
      "name": "John Doe",
      "phone": "99112233",
      "walletId": "w1w2w3w4-w5w6-w7w8-w9w0-w1w2w3w4w5w6"
    }
  ],
  "timestamp": "2023-10-27T10:40:00.123456"
}
```

---

## Top-up (Wallet Funding)

### 1. Initiate Top-up
**Endpoint:** `POST /api/v1/topup/initiate`

**Description:** Initiates a wallet top-up request.

**Request Body:**
```json
{
  "amount": 5000.00 // Required, min 1.0
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Top-up initiated successfully",
  "data": {
    "transactionId": 12345,
    "amount": 5000.00,
    "status": "PENDING",
    "provider": "QPAY",
    "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUg...", // Base64 encoded QR code or URL
    "createdAt": "2023-10-27T10:10:00"
  },
  "timestamp": "2023-10-27T10:10:00.123456"
}
```

### 2. Confirm Top-up
**Endpoint:** `POST /api/v1/topup/confirm/{transactionId}`

**Description:** Manually confirms a top-up transaction (useful for testing or specific flows).

**Path Parameters:**
*   `transactionId`: ID of the transaction to confirm.

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Top-up completed successfully",
  "data": {
    "transactionId": 12345,
    "amount": 5000.00,
    "status": "COMPLETED",
    "provider": "QPAY",
    "qrCode": null,
    "createdAt": "2023-10-27T10:10:00"
  },
  "timestamp": "2023-10-27T10:15:00.123456"
}
```

### 3. Get Top-up History
**Endpoint:** `GET /api/v1/topup/history`

**Description:** Retrieves the history of top-up transactions for the current user.

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Top-up history retrieved successfully",
  "data": [
    {
      "transactionId": 12345,
      "amount": 5000.00,
      "status": "COMPLETED",
      "provider": "QPAY",
      "qrCode": null,
      "createdAt": "2023-10-27T10:10:00"
    },
    {
      "transactionId": 12344,
      "amount": 10000.00,
      "status": "FAILED",
      "provider": "QPAY",
      "qrCode": null,
      "createdAt": "2023-10-26T15:30:00"
    }
  ],
  "timestamp": "2023-10-27T10:20:00.123456"
}
```

---

## Wallet

### 1. Get My Wallet (Balance)
**Endpoint:** `GET /api/v1/wallets/my-wallet`

**Description:** Retrieves the current user's wallet details, including balance.

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Wallet retrieved successfully",
  "data": {
    "id": "w1w2w3w4-w5w6-w7w8-w9w0-w1w2w3w4w5w6",
    "userId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
    "balance": 15000.00,
    "currency": "MNT",
    "createdAt": "2023-10-27T10:00:00"
  },
  "timestamp": "2023-10-27T10:25:00.123456"
}
```

---

## Transactions (Income & Expense)

> **Note:** All transaction responses include a `flow` field that indicates the transaction direction from the current user's perspective:
> - `INFLOW`: Money received (income)
> - `OUTFLOW`: Money sent (expense)

### 1. Send Money (Transfer)
**Endpoint:** `POST /api/v1/transactions/send`

**Description:** Transfers money from the current user's wallet to another wallet.

**Request Body:**
```json
{
  "toWalletId": "w9w0-w1w2w3w4w5w6-w1w2w3w4-w5w6-w7w8", // Required, UUID
  "amount": 1000.00,                                   // Required, min 0.01
  "description": "Lunch money"                         // Optional, max 255 chars
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Transaction completed successfully",
  "data": {
    "id": "t1t2t3t4-t5t6-t7t8-t9t0-t1t2t3t4t5t6",
    "fromWalletId": "w1w2w3w4-w5w6-w7w8-w9w0-w1w2w3w4w5w6",
    "toWalletId": "w9w0-w1w2w3w4w5w6-w1w2w3w4-w5w6-w7w8",
    "amount": 1000.00,
    "type": "SEND",
    "status": "COMPLETED",
    "description": "Lunch money",
    "createdAt": "2023-10-27T10:30:00",
    "flow": "OUTFLOW"
  },
  "timestamp": "2023-10-27T10:30:00.123456"
}
```

### 2. Get Transaction History
**Endpoint:** `GET /api/v1/transactions/history`

**Description:** Retrieves the full transaction history (both income and expense) for the current user.

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Transaction history retrieved successfully",
  "data": [
    {
      "id": "t1t2t3t4-t5t6-t7t8-t9t0-t1t2t3t4t5t6",
      "fromWalletId": "w1w2w3w4-w5w6-w7w8-w9w0-w1w2w3w4w5w6",
      "toWalletId": "w9w0-w1w2w3w4w5w6-w1w2w3w4-w5w6-w7w8",
      "amount": 1000.00,
      "type": "SEND",
      "status": "COMPLETED",
      "description": "Lunch money",
      "createdAt": "2023-10-27T10:30:00",
      "flow": "OUTFLOW"
    },
    {
      "id": "t9t0-t1t2t3t4t5t6-t1t2t3t4-t5t6-t7t8",
      "fromWalletId": "w9w0-w1w2w3w4w5w6-w1w2w3w4-w5w6-w7w8",
      "toWalletId": "w1w2w3w4-w5w6-w7w8-w9w0-w1w2w3w4w5w6",
      "amount": 500.00,
      "type": "SEND",
      "status": "COMPLETED",
      "description": "Refund",
      "createdAt": "2023-10-26T14:20:00",
      "flow": "INFLOW"
    }
  ],
  "timestamp": "2023-10-27T10:35:00.123456"
}
```

### 3. Get Recent Transactions
**Endpoint:** `GET /api/v1/transactions/recent`

**Description:** Retrieves the most recent transactions for the current user (e.g., last 5 or 10).

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Recent transactions retrieved successfully",
  "data": [
    {
      "id": "t1t2t3t4-t5t6-t7t8-t9t0-t1t2t3t4t5t6",
      "fromWalletId": "w1w2w3w4-w5w6-w7w8-w9w0-w1w2w3w4w5w6",
      "toWalletId": "w9w0-w1w2w3w4w5w6-w1w2w3w4-w5w6-w7w8",
      "amount": 1000.00,
      "type": "SEND",
      "status": "COMPLETED",
      "description": "Lunch money",
      "createdAt": "2023-10-27T10:30:00",
      "flow": "OUTFLOW"
    }
  ],
  "timestamp": "2023-10-27T10:35:00.123456"
}
```

---

## Payment Cards

> **Security Note**: All card data is tokenized via payment gateways (Stripe, QPay, etc.). Full card numbers and CVV codes are NEVER stored in the database.

### 1. Add Payment Card
**Endpoint:** `POST /api/v1/cards`

**Description:** Adds a new payment card to the user's account. The card is tokenized via a payment gateway before being stored.

**Request Body:**
```json
{
  "cardHolderName": "John Doe",           // Required, max 255 characters
  "cardNumberLast4": "4242",              // Required, exactly 4 digits
  "cardType": "VISA",                     // Required: VISA, MASTERCARD, AMERICAN_EXPRESS, DISCOVER, JCB, UNIONPAY, OTHER
  "expiryMonth": 12,                      // Required, 1-12
  "expiryYear": 2025,                     // Required, must be in the future
  "cardToken": "tok_1234567890",          // Required, token from payment gateway
  "isDefault": true                       // Optional, default: false
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Card added successfully",
  "data": {
    "id": "c1c2c3c4-c5c6-c7c8-c9c0-c1c2c3c4c5c6",
    "cardHolderName": "John Doe",
    "cardNumberLast4": "4242",
    "cardType": "VISA",
    "expiryMonth": 12,
    "expiryYear": 2025,
    "isDefault": true,
    "isVerified": false,
    "createdAt": "2023-10-27T10:40:00"
  },
  "timestamp": "2023-10-27T10:40:00.123456"
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "This card has already been added",
  "errorCode": "BAD_REQUEST",
  "data": null,
  "timestamp": "2023-10-27T10:40:00.123456"
}
```

---

### 2. Get All Payment Cards
**Endpoint:** `GET /api/v1/cards`

**Description:** Retrieves all payment cards for the current user, ordered by default status and creation date (default cards first).

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Cards retrieved successfully",
  "data": [
    {
      "id": "c1c2c3c4-c5c6-c7c8-c9c0-c1c2c3c4c5c6",
      "cardHolderName": "John Doe",
      "cardNumberLast4": "4242",
      "cardType": "VISA",
      "expiryMonth": 12,
      "expiryYear": 2025,
      "isDefault": true,
      "isVerified": true,
      "createdAt": "2023-10-27T10:40:00"
    },
    {
      "id": "c9c0-c1c2c3c4c5c6-c1c2c3c4-c5c6-c7c8",
      "cardHolderName": "John Doe",
      "cardNumberLast4": "5555",
      "cardType": "MASTERCARD",
      "expiryMonth": 6,
      "expiryYear": 2026,
      "isDefault": false,
      "isVerified": true,
      "createdAt": "2023-10-26T14:20:00"
    }
  ],
  "timestamp": "2023-10-27T10:45:00.123456"
}
```

**Success Response (Empty List):**
```json
{
  "success": true,
  "message": "Cards retrieved successfully",
  "data": [],
  "timestamp": "2023-10-27T10:45:00.123456"
}
```

---

### 3. Get Default Payment Card
**Endpoint:** `GET /api/v1/cards/default`

**Description:** Retrieves the user's default payment card.

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Default card retrieved successfully",
  "data": {
    "id": "c1c2c3c4-c5c6-c7c8-c9c0-c1c2c3c4c5c6",
    "cardHolderName": "John Doe",
    "cardNumberLast4": "4242",
    "cardType": "VISA",
    "expiryMonth": 12,
    "expiryYear": 2025,
    "isDefault": true,
    "isVerified": true,
    "createdAt": "2023-10-27T10:40:00"
  },
  "timestamp": "2023-10-27T10:50:00.123456"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "No default card found",
  "errorCode": "RESOURCE_NOT_FOUND",
  "data": null,
  "timestamp": "2023-10-27T10:50:00.123456"
}
```

---

### 4. Delete Payment Card
**Endpoint:** `DELETE /api/v1/cards/{cardId}`

**Description:** Deletes a payment card from the user's account. If the deleted card was the default, another card will automatically be set as default.

**Path Parameters:**
* `cardId`: UUID of the card to delete

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Card deleted successfully",
  "data": null,
  "timestamp": "2023-10-27T10:55:00.123456"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Card not found",
  "errorCode": "RESOURCE_NOT_FOUND",
  "data": null,
  "timestamp": "2023-10-27T10:55:00.123456"
}
```

**Error Response (403 Forbidden):**
```json
{
  "success": false,
  "message": "You do not have permission to delete this card",
  "errorCode": "BAD_REQUEST",
  "data": null,
  "timestamp": "2023-10-27T10:55:00.123456"
}
```

---

### 5. Set Default Payment Card
**Endpoint:** `PUT /api/v1/cards/{cardId}/default`

**Description:** Sets the specified card as the user's default payment card. The previous default card will be automatically unmarked.

**Path Parameters:**
* `cardId`: UUID of the card to set as default

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Default card updated successfully",
  "data": {
    "id": "c9c0-c1c2c3c4c5c6-c1c2c3c4-c5c6-c7c8",
    "cardHolderName": "John Doe",
    "cardNumberLast4": "5555",
    "cardType": "MASTERCARD",
    "expiryMonth": 6,
    "expiryYear": 2026,
    "isDefault": true,
    "isVerified": true,
    "createdAt": "2023-10-26T14:20:00"
  },
  "timestamp": "2023-10-27T11:00:00.123456"
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Card not found",
  "errorCode": "RESOURCE_NOT_FOUND",
  "data": null,
  "timestamp": "2023-10-27T11:00:00.123456"
}
```

**Error Response (403 Forbidden):**
```json
{
  "success": false,
  "message": "You do not have permission to modify this card",
  "errorCode": "BAD_REQUEST",
  "data": null,
  "timestamp": "2023-10-27T11:00:00.123456"
}
```

