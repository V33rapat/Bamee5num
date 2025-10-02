# Product Requirements Document: Merge Feature Branch to Main

## Introduction/Overview

Merge the `feature/seperate-customer-cart` branch into the `main` branch to integrate the enhanced customer cart management system with the newly developed Employee and Manager functionality. The main branch has evolved significantly since the feature branch was created, adding comprehensive Employee/Manager features including menu management, employee login system, and manager dashboard capabilities.

**Branch Status:**
- **Feature Branch:** `origin/feature/seperate-customer-cart` (Ready to merge, last commit: 8f87e5c)
- **Target Branch:** `main` (Has 12 additional commits beyond feature branch)
- **Common Ancestor:** 535ef787b9fb3dee7f9c41520aa2f61c100c2c7a

## Goals

1. Successfully merge the customer cart enhancements from feature branch into main
2. Preserve all Employee/Manager functionality added to main
3. Resolve conflicts in CartController and CartService while maintaining both feature sets
4. Ensure the merged application supports:
   - Customer authentication and cart management (from feature branch)
   - Employee login with 6-digit codes (from main)
   - Manager dashboard with menu CRUD operations (from main)
5. Update dependencies to use the latest compatible versions
6. Remove deprecated code and consolidate implementations

## User Stories

- **As a customer**, I want to use the enhanced cart system with proper validation and ownership checks from the feature branch
- **As an employee**, I want to login using my 6-digit code and access employee-specific features
- **As a manager**, I want to access the dashboard to manage menu items and view reports
- **As a developer**, I want a clean codebase without merge conflict markers that compiles and runs successfully

## Functional Requirements

### FR-1: Dependency Management
The system must use Spring Boot 3.5.6 (from main) and include all necessary dependencies:
- spring-boot-starter-validation (from feature branch)
- spring-boot-starter-security (from feature branch)
- mysql-connector-j 9.4.0 (from feature branch)
- spring-boot-devtools (from main)

### FR-2: CartController Integration
The CartController must support BOTH approaches:
- **Simple approach** (from main): Constructor injection, simple endpoints for basic cart operations
- **Enhanced approach** (from feature branch): Comprehensive validation, customer ownership checks, detailed cart operations

Resolution: Adopt the feature branch's comprehensive implementation as it's more secure and production-ready.

### FR-3: CartService Integration  
The CartService must:
- Support customer-specific cart operations with ownership validation (feature branch)
- Maintain compatibility with Employee/Manager systems (main)
- Remove deprecated methods and use the new API consistently

### FR-4: Model Entity Preservation
Preserve ALL entity models:
- **Customer** and **CartItem** (both branches, ensure feature branch validation remains)
- **Employee**, **Manager**, **MenuItem**, **User**, **MenuManageble** (main only)

### FR-5: Controller Layer Complete Integration
Maintain all controllers:
- **CustomerController**: Use feature branch version (more comprehensive)
- **CartController**: Use feature branch version (comprehensive validation)
- **EmployeeApiController**, **ManagerApiController**, **MenuItemController**, **AuthController** (main only)

### FR-6: Service Layer Integration
Preserve all services:
- **CustomerService**, **CustomUserDetailsService**: Feature branch versions
- **CartService**: Merge both, preferring feature branch architecture
- **Employee services**, **Manager services**, **User services**, **MenuItemService**, **DataService** (main only)

### FR-7: Configuration Harmonization
- Use application.properties from main (has employee/manager settings)
- Maintain security configuration from feature branch
- Ensure all entity models are properly recognized by JPA

### FR-8: Frontend Integration
- Preserve manager.js and manager.html enhancements from main
- Keep landing.js improvements from both branches
- Ensure customer.js works with the merged CartController

## Non-Goals (Out of Scope)

- Refactoring the Employee/Manager architecture (accept as-is from main)
- Adding new features beyond what exists in both branches
- Database migration scripts (assume manual database updates)
- Test implementation (test folders are empty in both branches)
- Removing the in-memory implementations for Employee/Manager (keep for now)

## Design Considerations

### Architecture Decision: Cart Service API
**Decision:** Use the feature branch's customer-centric CartService API as the primary interface.

**Rationale:**
- More secure (ownership validation)
- Better validation (Jakarta Bean Validation)
- Clearer separation of concerns
- Follows REST best practices

### Conflict Resolution Strategy

**High Priority Conflicts:**
1. **CartController.java**: Use feature branch version entirely (more comprehensive)
2. **CartService.java**: Use feature branch as base, ensure compatibility with Integer customerId
3. **CartItem.java**: Use feature branch version (has proper validation)

**Configuration Conflicts:**
1. **pom.xml**: Use main's Spring Boot version (3.5.6), add feature branch dependencies
2. **application.properties**: Use main's version, ensure MySQL settings match feature branch

**File Additions from Main:**
- Accept all new controllers (Auth, Employee, Manager, MenuItem)
- Accept all new models (Employee, Manager, MenuItem, User, MenuManageble)
- Accept all new services and DTOs for Employee/Manager functionality

## Technical Considerations

### Dependency Compatibility
- Spring Boot 3.5.6 is backward compatible with 3.5.5
- Both versions use Java 17
- MySQL connector version 9.4.0 works with both

### Database Schema Impact
Main branch likely added:
- `employees` table
- `managers` table  
- `menu_items` table
- `users` table (if using shared authentication)

Feature branch expects:
- `customers` table
- `cart_items` table

**Action Required:** Database will need migration to include all tables.

### API Endpoint Coexistence
Ensure no endpoint conflicts:
- Customer APIs: `/api/customers/**`, `/api/cart/**`
- Employee APIs: `/api/employees/**` (if exists)
- Manager APIs: `/api/manager/**` (if exists)
- Menu APIs: `/api/menu/**` or similar
- Auth APIs: `/api/auth/**` (if exists)

## Success Metrics

1. **Build Success**: `mvn clean compile` completes without errors
2. **No Conflict Markers**: No `<<<<<<<`, `=======`, `>>>>>>>` markers remain in any file
3. **Application Starts**: `mvn spring-boot:run` successfully starts the application
4. **Customer Features Work**: Can register, login, add to cart, view cart
5. **Employee Features Work**: Can login with 6-digit code
6. **Manager Features Work**: Can access dashboard, manage menu items
7. **Database Compatibility**: Application connects to MySQL and initializes schema

## Open Questions

1. **Q:** Does the Employee login system conflict with Customer authentication?  
   **A:** Need to verify both can coexist (likely different user types)

2. **Q:** Are there any runtime dependencies between Customer cart and Menu items?  
   **A:** Check if CartItem references MenuItem or just stores item name/price

3. **Q:** Should deprecated methods in CartService be removed immediately?  
   **A:** Yes, remove them as part of merge cleanup

4. **Q:** Is the PROJECT_OVERVIEW.md in main needed, or use the one we just created?  
   **A:** Main branch version should be removed (it's outdated); feature branch doesn't have it, regenerate after merge

5. **Q:** What about the task files in feature branch (prd-customer-specific-pages.md, etc.)?  
   **A:** These are development artifacts, can be kept for historical reference

## Risk Assessment

### High Risk
- **CartService merge**: Complex logic differences, requires careful review
- **Application startup**: Multiple authentication mechanisms might conflict
- **Database connectivity**: Configuration differences between branches

### Medium Risk  
- **Dependency conflicts**: Version mismatches in pom.xml
- **Security configuration**: May need to support multiple user types
- **Frontend JavaScript**: Cart operations might call wrong endpoints

### Low Risk
- **Static assets**: CSS unlikely to conflict
- **Template files**: Only manager.html modified in main
- **Test files**: Empty in both branches

## Merge Conflict Resolution Guidelines

### For Each Conflict:

1. **Understand both versions**: What does each branch do?
2. **Identify intent**: Why was the change made?
3. **Choose strategy**: 
   - Accept feature (newer, more comprehensive)
   - Accept main (new functionality)
   - Merge both (combine features)
4. **Test the decision**: Ensure it compiles
5. **Document if unclear**: Add comments explaining complex resolutions

### Conflict Categories:

**Type 1: Pure Addition** (main added new files)
- **Resolution:** Accept all additions from main

**Type 2: Pure Enhancement** (feature branch improved existing)
- **Resolution:** Accept feature branch version

**Type 3: Divergent Evolution** (both modified same file differently)
- **Resolution:** Requires manual merge, prefer more comprehensive version

**Type 4: Configuration** (build/config files)
- **Resolution:** Merge both changes, prefer newer versions

## Post-Merge Validation Checklist

- [ ] All Java files compile without errors
- [ ] No merge conflict markers remain
- [ ] pom.xml is well-formed and includes all dependencies
- [ ] application.properties has correct database configuration
- [ ] Application starts without exceptions
- [ ] Can access landing page at http://localhost:8080
- [ ] Customer registration works
- [ ] Customer login works  
- [ ] Cart operations work (add, update, remove)
- [ ] Employee login works (if implemented)
- [ ] Manager dashboard loads (if implemented)
- [ ] Database schema matches all entity models
- [ ] No deprecated method warnings in compilation

---

**Document Status:** ✅ Ready for Task Generation  
**Created:** October 2, 2025  
**Last Updated:** October 2, 2025
