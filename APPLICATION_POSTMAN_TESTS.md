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

---

## 10. Paginated Get Applications (First Page)
* **Endpoint:** `GET /api/v1/applications?page=0&size=5`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns a page JSON containing an array of up to 5 applications in `content`, and page metadata fields:
    ```json
    {
      "content": [...],
      "pageable": {...},
      "totalElements": 6,
      "totalPages": 2,
      "last": false,
      "size": 5,
      "number": 0,
      "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
      },
      "numberOfElements": 5,
      "first": true,
      "empty": false
    }
    ```

---

## 11. Paginated Get Applications (Next Page)
* **Endpoint:** `GET /api/v1/applications?page=1&size=5`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns the next page JSON containing the remaining applications in `content`, and metadata:
    ```json
    {
      "content": [...],
      "totalElements": 6,
      "totalPages": 2,
      "last": true,
      "size": 5,
      "number": 1,
      "numberOfElements": 1,
      "first": false,
      "empty": false
    }
    ```

---

## 12. Get Applications Filtered by Status
* **Endpoint:** `GET /api/v1/applications?status=INTERVIEW`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns a page JSON containing only applications with `status = "INTERVIEW"` in `content`.

---

## 13. Get Applications Filtered by Company (Case-Insensitive Partial Match)
* **Endpoint:** `GET /api/v1/applications?company=google`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns a page JSON containing only applications whose company name contains "google" (case-insensitive partial match) in `content`.

---

## 14. Get Applications Filtered by Date Applied Range
* **Endpoint:** `GET /api/v1/applications?fromDate=2026-01-01&toDate=2026-06-01`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns a page JSON containing applications with `applicationDate` between `2026-01-01` and `2026-06-01` (inclusive) in `content`.

---

## 15. Sort Whitelist Validation (Security Rejected)
* **Endpoint:** `GET /api/v1/applications?sort=maliciousField`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `400 Bad Request`
  * Body: Consistent validation error response:
    ```json
    {
      "message": "Invalid sort field: maliciousField",
      "status": 400,
      "timestamp": "2026-06-17..."
    }
    ```

---

## 16. Sort by dateApplied DESC (Allowed Field)
* **Endpoint:** `GET /api/v1/applications?sort=dateApplied&direction=DESC`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns a page JSON where applications in `content` are sorted from newest to oldest `applicationDate`.

---

## 17. Combined Search, Sort, and Paginated Query
* **Endpoint:** `GET /api/v1/applications?status=INTERVIEW&sort=dateApplied&direction=DESC&page=0&size=3`
* **Headers:**
  * `Authorization: Bearer <token>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns a page JSON containing at most 3 applications, only with status `INTERVIEW`, sorted by date newest first.

---

## 18. Regular User Dashboard API
* **Endpoint:** `GET /api/v1/dashboard`
* **Headers:**
  * `Authorization: Bearer <token_of_regular_user>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns user-specific application metrics:
    ```json
    {
      "totalApplications": 6,
      "applicationsByStatus": {
        "APPLIED": 1,
        "SHORTLISTED": 1,
        "ASSESSMENT": 0,
        "INTERVIEW": 1,
        "OFFER": 1,
        "ACCEPTED": 1,
        "REJECTED": 1
      },
      "interviewCount": 1,
      "rejectedCount": 1,
      "offerCount": 1,
      "acceptedCount": 1,
      "responseRate": 83.33333333333334,
      "offerRate": 33.33333333333333
    }
    ```

---

## 19. Admin User System-wide Dashboard API
* **Endpoint:** `GET /api/v1/dashboard`
* **Headers:**
  * `Authorization: Bearer <token_of_admin_user>`
* **Expected Response:**
  * Status: `200 OK`
  * Body: Returns system-wide application metrics (aggregated across all users):
    ```json
    {
      "totalApplications": 7,
      "applicationsByStatus": {
        "APPLIED": 1,
        "SHORTLISTED": 1,
        "ASSESSMENT": 0,
        "INTERVIEW": 2,
        "OFFER": 1,
        "ACCEPTED": 1,
        "REJECTED": 1
      },
      "interviewCount": 2,
      "rejectedCount": 1,
      "offerCount": 1,
      "acceptedCount": 1,
      "responseRate": 85.71428571428571,
      "offerRate": 28.57142857142857
    }
    ```
