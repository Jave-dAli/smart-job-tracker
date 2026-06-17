# Smart Job Tracker - Postman Test Cases Checklist

This document details the Postman test cases to verify the job applications functionality.

---

## 1. Create Application
* **Endpoint:** `POST /api/v1/applications`
* **Headers:**
  * `Content-Type: application/json`
  * `Authorization: Bearer <token>`
* **Request Body:**
  ```json
  {
    "companyName": "Google",
    "jobTitle": "Software Engineer",
    "applicationDate": "2026-06-17",
    "location": "Mountain View, CA",
    "salaryRange": "$150k - $200k",
    "jobUrl": "https://careers.google.com/jobs/1",
    "notes": "Referred by John Doe",
    "resumeFileId": null,
    "coverLetterFileId": null
  }
  ```
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns the created application response containing the allocated `id`, `status` (`APPLIED`), and matching fields.

---

## 2. Get All Applications
* **Endpoint:** `GET /api/v1/applications`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: An array containing the authenticated user's active (non-deleted) applications.

---

## 3. Get Application by ID
* **Endpoint:** `GET /api/v1/applications/{id}`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns the job application matching the requested `id`.

---

## 4. Update Application
* **Endpoint:** `PUT /api/v1/applications/{id}`
* **Headers:**
  * `Content-Type: application/json`
  * `Authorization: Bearer <token>`
* **Request Body:**
  ```json
  {
    "companyName": "Alphabet",
    "jobTitle": "Staff Software Engineer",
    "applicationDate": "2026-06-17",
    "location": "New York, NY",
    "salaryRange": "$200k - $250k",
    "jobUrl": "https://careers.google.com/jobs/2",
    "notes": "Updated notes info",
    "resumeFileId": null,
    "coverLetterFileId": null
  }
  ```
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns the updated application details.

---

## 5. Update Status (Valid Transition)
* **Endpoint:** `PATCH /api/v1/applications/{id}/status`
* **Headers:**
  * `Content-Type: application/json`
  * `Authorization: Bearer <token>`
* **Request Body:**
  ```json
  {
    "status": "SHORTLISTED"
  }
  ```
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns the application with status successfully transitioned to `SHORTLISTED`.

---

## 6. Update Status (Invalid Transition)
* **Endpoint:** `PATCH /api/v1/applications/{id}/status`
* **Headers:**
  * `Content-Type: application/json`
  * `Authorization: Bearer <token>`
* **Request Body:**
  ```json
  {
    "status": "ACCEPTED"
  }
  ```
* **Expected Response:**
  * Status: `422 Unprocessable Entity`
  * Body: Consistent error JSON:
    ```json
    {
      "message": "Invalid status transition: APPLIED/SHORTLISTED → ACCEPTED",
      "status": 422,
      "timestamp": "2026-06-17..."
    }
    ```

---

## 7. Soft Delete Application
* **Endpoint:** `DELETE /api/v1/applications/{id}`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `204 No Content`

---

## 8. Verify Deleted Record is Hidden
* **Endpoint:** `GET /api/v1/applications/{deleted_id}`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `404 Not Found`
  * Body: Consistent error JSON:
    ```json
    {
      "message": "Application not found",
      "status": 404,
      "timestamp": "2026-06-17..."
    }
    ```

---

## 9. Two-User Ownership Verification
* **Setup:**
  * User A creates application with `id = 123`.
  * User B attempts to view User A's application.
* **Endpoint:** `GET /api/v1/applications/123`
* **Headers:**
  * `Authorization: Bearer <token_of_user_b>`
* **Expected Response:**
  * Status: `404 Not Found`
  * Body: Consistent error JSON:
    ```json
    {
      "message": "Application not found",
      "status": 404,
      "timestamp": "2026-06-17..."
    }
    ```
