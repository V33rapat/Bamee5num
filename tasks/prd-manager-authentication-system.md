# Product Requirements Document: Manager Authentication System

## Introduction/Overview

Currently, the restaurant management system lacks a proper authentication mechanism for managers. Managers cannot access the manager dashboard because they are redirected to the customer login page, and the manager table only contains an `id` column without authentication credentials. This PRD outlines the implementation of a complete, separate authentication system for managers, including dedicated login/registration pages and proper database schema to enable secure manager access.

**Problem Statement:** Managers cannot log into the system because:
1. No dedicated manager login/registration pages exist
2. Manager table lacks authentication fields (username, email, password)
3. All authentication attempts redirect to customer login

**Solution:** Implement a separate manager authentication system with its own registration flow, login page, and complete user credentials management.

---

## Goals

1. Enable managers to self-register for accounts through a dedicated registration page
2. Provide a separate manager login page at a distinct URL path
3. Extend the manager database schema to include username, email, and password fields
4. Implement secure password storage using industry-standard hashing (BCrypt)
5. Ensure complete separation between customer and manager authentication flows
6. Allow managers to successfully access the manager dashboard after authentication

---

## User Stories

1. **As a new manager**, I want to register for an account using my email, username, and password, so that I can access the manager dashboard.

2. **As a registered manager**, I want to log in through a dedicated manager login page, so that I don't get confused with the customer login interface.

3. **As a manager**, I want my password to be securely stored, so that my account remains protected.

4. **As a manager**, I want to be immediately activated after registration without email verification, so that I can start working right away.

5. **As a system administrator**, I want manager and customer authentication to be completely separated, so that there's no conflict or cross-contamination between the two user types.

6. **As a manager**, I want my session to be separate from customer sessions, so that my authentication state is managed independently.

---

## Functional Requirements

### Database Schema

1. The `manager` table must be updated to include the following columns:
   - `id` (existing, primary key, auto-increment)
   - `username` (VARCHAR(50), unique, not null)
   - `email` (VARCHAR(100), unique, not null) - used as login identifier
   - `password` (VARCHAR(255), not null) - stored as BCrypt hash
   - `created_at` (TIMESTAMP, default CURRENT_TIMESTAMP)
   - `updated_at` (TIMESTAMP, default CURRENT_TIMESTAMP ON UPDATE)

### Registration Functionality

2. The system must provide a manager registration page accessible at `/manager/register`
3. The registration form must include fields for:
   - Username (required, unique)
   - Email (required, unique, valid email format)
   - Password (required, minimum 8 characters)
   - Confirm Password (required, must match password)
4. The system must validate that the email is not already registered
5. The system must validate that the username is not already taken
6. The system must hash passwords using BCrypt before storing them in the database
7. Upon successful registration, the manager account must be immediately activated
8. After successful registration, the system must redirect the manager to the manager login page with a success message

### Login Functionality

9. The system must provide a dedicated manager login page at `/manager/login`
10. The login form must include fields for:
    - Email (required)
    - Password (required)
11. The system must authenticate managers using email and password credentials
12. The system must verify the password against the BCrypt hash stored in the database
13. Upon successful login, the system must create a manager session
14. After successful login, the system must redirect the manager to `/manager` (manager dashboard)
15. Failed login attempts must display an appropriate error message (e.g., "Invalid email or password")
16. The system must not allow customers to log in through the manager login page

### Session Management

17. The system must maintain separate session management for managers and customers
18. Manager sessions must be stored independently from customer sessions
19. The system must track the authenticated manager's ID, username, and email in the session
20. Accessing the manager dashboard (`/manager`) must require an active manager session
21. Unauthenticated access attempts to manager pages must redirect to `/manager/login`

### Security

22. All passwords must be hashed using BCrypt with a minimum work factor of 10
23. The system must protect against SQL injection in all database queries
24. The system must implement CSRF protection on registration and login forms
25. Password fields must be masked in the UI (type="password")
26. The system must enforce HTTPS in production (configuration recommendation)

### Logout Functionality

27. The system must provide a logout mechanism for managers
28. Logout must invalidate the manager session
29. After logout, the system must redirect to `/manager/login`

---

## Non-Goals (Out of Scope)

1. **Email verification** - Managers will be activated immediately without email confirmation
2. **Password reset/forgot password functionality** - Not included in initial implementation
3. **Multi-level manager roles** - Single manager role with full access (Admin, Supervisor roles not included)
4. **Two-factor authentication (2FA)** - Not included in initial implementation
5. **Manager profile management** - No ability to update profile information beyond initial registration
6. **Integration with customer authentication** - Managers and customers remain completely separate
7. **Advanced password requirements** - Only basic requirement (minimum 8 characters) enforced
8. **Account deactivation/suspension** - No admin panel to manage manager accounts
9. **Migration of existing manager data** - Starting with fresh manager table structure

---

## Design Considerations

### UI/UX Requirements

1. **Manager Registration Page** (`/manager/register`):
   - Form layout similar to customer registration for consistency
   - Clear heading: "Manager Registration"
   - Input fields with appropriate labels and placeholders
   - Client-side validation feedback (e.g., password length, email format)
   - Submit button labeled "Register"
   - Link to manager login page: "Already have an account? Login here"

2. **Manager Login Page** (`/manager/login`):
   - Form layout similar to customer login for consistency
   - Clear heading: "Manager Login"
   - Email and password input fields
   - Submit button labeled "Login"
   - Link to manager registration page: "Don't have an account? Register here"
   - Error messages displayed prominently on validation failure

3. **Styling:**
   - Reuse existing CSS styles from `static/css/style.css`
   - Maintain visual consistency with the existing application
   - Ensure responsive design for mobile and desktop views

### URL Structure

- Manager Registration: `/manager/register`
- Manager Login: `/manager/login`
- Manager Dashboard: `/manager` (existing)
- Manager Logout: `/manager/logout`

### Navigation

- Customer-facing pages should not link to manager pages
- Manager pages should include a logout button/link
- Attempting to access manager pages without authentication redirects to `/manager/login`

---

## Technical Considerations

### Backend (Spring Boot)

1. **Entity/Model:**
   - Create or update `Manager.java` entity with fields: id, username, email, password, createdAt, updatedAt
   - Add appropriate JPA annotations (@Entity, @Table, @Column, @Id, @GeneratedValue)

2. **Repository:**
   - Create `ManagerRepository.java` extending JpaRepository
   - Add custom query methods: `findByEmail`, `existsByEmail`, `existsByUsername`

3. **Service:**
   - Create `ManagerService.java` with methods for:
     - `registerManager(ManagerRegistrationDTO)` - handles registration logic
     - `authenticateManager(email, password)` - handles login authentication
     - `getManagerByEmail(email)` - retrieves manager by email

4. **Controller:**
   - Create `ManagerAuthController.java` with endpoints:
     - `GET /manager/register` - displays registration form
     - `POST /manager/register` - processes registration
     - `GET /manager/login` - displays login form
     - `POST /manager/login` - processes login
     - `POST /manager/logout` - handles logout

5. **DTO (Data Transfer Objects):**
   - Create `ManagerRegistrationDTO.java` with validation annotations
   - Create `ManagerLoginDTO.java` with validation annotations

6. **Security Configuration:**
   - Update `SecurityConfig.java` to:
     - Permit access to `/manager/register` and `/manager/login` without authentication
     - Require authentication for `/manager/**` (manager dashboard and related pages)
     - Configure separate session management for manager endpoints
   - Use BCryptPasswordEncoder for password hashing

7. **Session Management:**
   - Use Spring Security's session management
   - Store manager authentication in a separate security context
   - Configure session attributes to track manager user details

### Frontend (HTML/JavaScript)

1. **HTML Templates:**
   - Create `manager-register.html` in `templates/` directory
   - Create `manager-login.html` in `templates/` directory
   - Update `manager.html` to include logout functionality

2. **JavaScript:**
   - Create `manager-auth.js` in `static/js/` for client-side validation
   - Implement form validation before submission
   - Display error/success messages dynamically

3. **CSS:**
   - Reuse existing styles from `style.css`
   - Add manager-specific styles if needed

### Database

1. **Migration:**
   - Create SQL migration script to alter the `manager` table
   - Add columns: username, email, password, created_at, updated_at
   - Add unique constraints on email and username

2. **Example SQL:**
   ```sql
   ALTER TABLE manager 
   ADD COLUMN username VARCHAR(50) UNIQUE NOT NULL,
   ADD COLUMN email VARCHAR(100) UNIQUE NOT NULL,
   ADD COLUMN password VARCHAR(255) NOT NULL,
   ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
   ```

### Dependencies

- Spring Boot Starter Web (existing)
- Spring Boot Starter Data JPA (existing)
- Spring Boot Starter Security (existing)
- Spring Boot Starter Thymeleaf (existing)
- BCrypt (included in Spring Security)
- Spring Boot Starter Validation (for DTO validation)

---

## Success Metrics

1. **Functional Success:**
   - Managers can successfully register for new accounts
   - Managers can successfully log in using their email and password
   - Managers can access the manager dashboard after authentication
   - Unauthenticated users are redirected to `/manager/login` when accessing manager pages

2. **Security Success:**
   - All passwords are stored as BCrypt hashes (never plain text)
   - Manager and customer authentication remains completely separated
   - No authentication bypasses or vulnerabilities introduced

3. **User Experience:**
   - Registration and login processes complete in under 10 seconds
   - Clear error messages guide users when validation fails
   - UI is consistent with existing application design

4. **Technical Success:**
   - All database constraints (unique email, unique username) are enforced
   - Session management correctly maintains manager authentication state
   - No conflicts between customer and manager sessions

---

## Open Questions

1. Should there be a character limit for usernames? (e.g., minimum 3, maximum 50 characters)
2. Should email addresses be case-insensitive during login? (recommended: yes)
3. Should there be any logging/audit trail for manager login attempts?
4. What should happen if a manager tries to access customer pages while logged in as a manager?
5. Should there be a "Remember Me" functionality for manager login?
6. Should there be rate limiting on login attempts to prevent brute force attacks?

---

## Implementation Notes for Developers

### Recommended Implementation Order

1. **Database First:** Update the `manager` table schema
2. **Backend Models:** Create/update Manager entity and DTOs
3. **Backend Repository:** Create ManagerRepository with query methods
4. **Backend Service:** Implement ManagerService with business logic
5. **Backend Controller:** Create ManagerAuthController with endpoints
6. **Security Configuration:** Update SecurityConfig for manager routes
7. **Frontend Templates:** Create manager-register.html and manager-login.html
8. **Frontend JavaScript:** Add client-side validation
9. **Testing:** Test registration, login, logout, and access control
10. **Integration:** Verify manager dashboard access works correctly

### Key Security Reminders

- **Never store plain text passwords** - always use BCryptPasswordEncoder
- **Validate all inputs** - use Spring Validation annotations (@NotBlank, @Email, @Size)
- **Sanitize user inputs** - protect against XSS and SQL injection
- **Use parameterized queries** - JPA/Hibernate provides this by default
- **Enable CSRF protection** - ensure forms include CSRF tokens (Thymeleaf does this automatically)

### Testing Checklist

- [ ] Manager can register with valid credentials
- [ ] Registration fails with duplicate email
- [ ] Registration fails with duplicate username
- [ ] Registration fails with invalid email format
- [ ] Registration fails with password < 8 characters
- [ ] Registration fails when passwords don't match
- [ ] Manager can log in with correct credentials
- [ ] Login fails with incorrect password
- [ ] Login fails with non-existent email
- [ ] Manager is redirected to dashboard after login
- [ ] Unauthenticated access to `/manager` redirects to `/manager/login`
- [ ] Manager can log out successfully
- [ ] After logout, manager cannot access dashboard without logging in again
- [ ] Manager session does not interfere with customer session
- [ ] Customer cannot access manager login/registration pages and vice versa

---

## Appendix: Example Code Snippets

### Manager Entity Example
```java
@Entity
@Table(name = "manager")
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Getters, setters, constructors
}
```

### ManagerRegistrationDTO Example
```java
public class ManagerRegistrationDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
    
    // Getters and setters
}
```

---

**Document Version:** 1.0  
**Created:** October 4, 2025  
**Status:** Ready for Implementation
