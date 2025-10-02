# Task List: Merge Feature Branch to Main

**PRD Reference:** `prd-merge-feature-branch-to-main.md`  
**Branch:** `main` ← `origin/feature/seperate-customer-cart`  
**Status:** Ready for execution

---

## Relevant Files

### High-Conflict Files (Require Manual Resolution)
- `Project Principle/demo/pom.xml` - Maven dependencies, need to merge both sets of additions
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/CartController.java` - Complete rewrite between branches
- `Project Principle/demo/src/main/java/com/restaurant/demo/service/CartService.java` - Divergent implementations
- `Project Principle/demo/src/main/java/com/restaurant/demo/model/CartItem.java` - Validation and field differences
- `Project Principle/demo/src/main/resources/application.properties` - Configuration differences

### Medium-Conflict Files (Likely Need Review)
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/CustomerController.java` - Session handling differences
- `Project Principle/demo/src/main/resources/static/js/landing.js` - Frontend behavior changes
- `Project Principle/demo/src/main/resources/static/js/manager.js` - Manager features vs cleanup
- `Project Principle/demo/src/main/resources/templates/manager.html` - UI enhancements in main

### New Files from Main (Accept All)
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/AuthController.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/EmployeeApiController.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/ManagerApiController.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/MenuItemController.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/auth/LoginRequest.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/auth/UserView.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/model/Employee.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/model/Manager.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/model/MenuItem.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/model/MenuManageble`
- `Project Principle/demo/src/main/java/com/restaurant/demo/model/User.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/repository/MenuItemRepo.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/service/DataService.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/service/MenuItemService.java`
- `Project Principle/demo/src/main/java/com/restaurant/demo/service/employee/**` (all files)
- `Project Principle/demo/src/main/java/com/restaurant/demo/service/manager/**` (all files)
- `Project Principle/demo/src/main/java/com/restaurant/demo/service/user/**` (all files)
- `Project Principle/demo/run-server.ps1`
- `.project`
- `.vscode/launch.json`

### Files to Remove/Cleanup
- `PROJECT_OVERVIEW.md` (outdated in main, will regenerate after merge)
- `Project Principle/Readme.txt` (main version, feature has Readme.txt.txt)

### Test Files
- All test directories are empty in both branches - no conflicts

---

## Notes

- **Git Strategy:** We're merging FROM feature branch TO main (main needs updating)
- **Conflict Resolution Priority:** Feature branch takes precedence for Customer/Cart, Main takes precedence for Employee/Manager
- **Database:** After merge, ensure database-setup.sql includes all entity tables
- **Validation:** Run `mvn clean compile` after each major conflict resolution
- **Testing:** Manual testing required - no automated tests exist yet

---

## Tasks

- [ ] 1.0 Prepare for Merge
  - [ ] 1.1 Ensure all changes in main are committed
  - [ ] 1.2 Create backup branch: `git branch backup-main-before-merge`
  - [ ] 1.3 Verify clean working directory: `git status`
  - [ ] 1.4 Fetch latest from feature branch: `git fetch origin feature/seperate-customer-cart`
  - [ ] 1.5 Document current main branch commit hash for rollback reference

- [ ] 2.0 Initiate Merge and Identify Conflicts
  - [ ] 2.1 Start merge: `git merge origin/feature/seperate-customer-cart --no-commit --no-ff`
  - [ ] 2.2 Review conflict summary: `git status`
  - [ ] 2.3 List all conflicted files: `git diff --name-only --diff-filter=U`
  - [ ] 2.4 Create merge conflict resolution log file for tracking decisions

- [ ] 3.0 Resolve pom.xml Conflicts
  - [ ] 3.1 Open `Project Principle/demo/pom.xml` and locate conflict markers
  - [ ] 3.2 Set Spring Boot version to 3.5.6 (from main - newer version)
  - [ ] 3.3 Merge dependencies: Include spring-boot-starter-validation (feature)
  - [ ] 3.4 Merge dependencies: Include spring-boot-starter-security (feature)
  - [ ] 3.5 Merge dependencies: Include mysql-connector-j 9.4.0 (feature)
  - [ ] 3.6 Merge dependencies: Include spring-boot-devtools (main)
  - [ ] 3.7 Ensure maven plugin has groupId specified (feature branch addition)
  - [ ] 3.8 Remove all conflict markers from pom.xml
  - [ ] 3.9 Validate XML syntax and structure
  - [ ] 3.10 Test: Run `mvn clean compile` to verify pom.xml is valid

- [ ] 4.0 Resolve CartController Conflicts
  - [ ] 4.1 Open `Project Principle/demo/src/main/java/com/restaurant/demo/controller/CartController.java`
  - [ ] 4.2 Analyze both versions: main has simple implementation, feature has comprehensive with validation
  - [ ] 4.3 **Decision:** Accept feature branch version entirely (more production-ready)
  - [ ] 4.4 Remove all main branch code between conflict markers
  - [ ] 4.5 Keep feature branch implementation: @Validated, comprehensive endpoints, ownership checks
  - [ ] 4.6 Ensure imports are complete (no merge marker fragments)
  - [ ] 4.7 Remove any duplicate imports or methods
  - [ ] 4.8 Verify all endpoints: /add, /update/{id}, /remove/{id}, /customer/{id}, /{id}, /clear/{id}, /total/{id}
  - [ ] 4.9 Ensure @CrossOrigin annotation is present
  - [ ] 4.10 Test: Compile the file individually to check for syntax errors

- [ ] 5.0 Resolve CartService Conflicts
  - [ ] 5.1 Open `Project Principle/demo/src/main/java/com/restaurant/demo/service/CartService.java`
  - [ ] 5.2 Identify conflicted sections: imports, fields, methods
  - [ ] 5.3 **Decision:** Use feature branch as base structure
  - [ ] 5.4 Resolve import conflicts: Keep feature branch imports (Customer, CustomerRepository, validation)
  - [ ] 5.5 Use feature branch field declarations: @Autowired CartItemRepository and CustomerRepository
  - [ ] 5.6 Keep all customer-centric public methods from feature branch
  - [ ] 5.7 Remove deprecated methods that throw UnsupportedOperationException
  - [ ] 5.8 Remove simple int-based methods from main (addToCart with int customerId)
  - [ ] 5.9 Ensure all methods have proper null checks and validation
  - [ ] 5.10 Remove Thai comment "เธเธฑเธ"เธเธฒเธฃเธ•เธฐเธเธฃเนเธฒเธชเธดเธเธเนเธฒ" if present from main
  - [ ] 5.11 Remove conflict markers completely
  - [ ] 5.12 Test: Compile the file to verify no syntax errors

- [ ] 6.0 Resolve CartItem Model Conflicts
  - [ ] 6.1 Open `Project Principle/demo/src/main/java/com/restaurant/demo/model/CartItem.java`
  - [ ] 6.2 Compare both versions: Check for field differences, validation annotations
  - [ ] 6.3 **Decision:** Use feature branch version (has comprehensive Jakarta validation)
  - [ ] 6.4 Ensure @Entity and @Table annotations are correct
  - [ ] 6.5 Verify relationship: @ManyToOne with Customer, proper FetchType.LAZY
  - [ ] 6.6 Confirm validation annotations: @NotNull, @NotBlank, @DecimalMin, @DecimalMax, @Min, @Max
  - [ ] 6.7 Ensure timestamp fields use @CreationTimestamp and @UpdateTimestamp
  - [ ] 6.8 Verify getTotalPrice() calculated field method exists
  - [ ] 6.9 Remove any main branch additions if they conflict with validation approach
  - [ ] 6.10 Test: Compile to verify entity structure is valid

- [ ] 7.0 Resolve application.properties Conflicts
  - [ ] 7.1 Open `Project Principle/demo/src/main/resources/application.properties`
  - [ ] 7.2 Compare database configurations between branches
  - [ ] 7.3 Use feature branch MySQL configuration (production-ready)
  - [ ] 7.4 Ensure database URL: jdbc:mysql://localhost:3306/restaurant_db
  - [ ] 7.5 Keep MySQL driver class: com.mysql.cj.jdbc.Driver
  - [ ] 7.6 Preserve JPA settings from feature branch: hibernate.ddl-auto=update, show-sql=true
  - [ ] 7.7 Add any additional settings from main (employee/manager specific if any)
  - [ ] 7.8 Keep session configuration from feature branch
  - [ ] 7.9 Keep connection pool settings from feature branch
  - [ ] 7.10 Remove conflict markers

- [ ] 8.0 Resolve CustomerController Conflicts (if any)
  - [ ] 8.1 Open `Project Principle/demo/src/main/java/com/restaurant/demo/controller/CustomerController.java`
  - [ ] 8.2 Check for conflicts in login/registration methods
  - [ ] 8.3 **Decision:** Use feature branch version (has session management)
  - [ ] 8.4 Ensure loginCustomer() stores customerId in session
  - [ ] 8.5 Verify all validation endpoints are present
  - [ ] 8.6 Remove conflict markers
  - [ ] 8.7 Test: Compile to verify

- [ ] 9.0 Accept All New Files from Main Branch
  - [ ] 9.1 Verify AuthController.java is added (git will auto-add if no conflict)
  - [ ] 9.2 Verify EmployeeApiController.java is added
  - [ ] 9.3 Verify ManagerApiController.java is added
  - [ ] 9.4 Verify MenuItemController.java is added
  - [ ] 9.5 Verify all Employee, Manager, MenuItem, User models are added
  - [ ] 9.6 Verify all employee/manager/user service packages are added
  - [ ] 9.7 Verify MenuItemRepo is added
  - [ ] 9.8 Verify DataService and MenuItemService are added
  - [ ] 9.9 Check that git status shows these as "added" not "conflicted"

- [ ] 10.0 Resolve Frontend JavaScript Conflicts
  - [ ] 10.1 Open `Project Principle/demo/src/main/resources/static/js/landing.js`
  - [ ] 10.2 Review differences between branches
  - [ ] 10.3 Merge both changes if they affect different sections
  - [ ] 10.4 If conflict is in same section, prefer version that works with merged backend
  - [ ] 10.5 Open `Project Principle/demo/src/main/resources/static/js/manager.js`
  - [ ] 10.6 **Decision:** Use main branch version (has manager functionality)
  - [ ] 10.7 Remove conflict markers from all JS files
  - [ ] 10.8 Verify no syntax errors in JavaScript

- [ ] 11.0 Resolve Template Conflicts
  - [ ] 11.1 Open `Project Principle/demo/src/main/resources/templates/manager.html`
  - [ ] 11.2 Compare both versions
  - [ ] 11.3 **Decision:** Use main branch version (has manager dashboard enhancements)
  - [ ] 11.4 Remove conflict markers
  - [ ] 11.5 Verify template syntax is valid (Thymeleaf)

- [ ] 12.0 Handle File Additions and Deletions
  - [ ] 12.1 Accept deletion of `Project Principle/Readme.txt` (main had it, feature doesn't)
  - [ ] 12.2 Accept addition of `Project Principle/Readme.txt.txt` (feature has it)
  - [ ] 12.3 Accept addition of `.project` file from main
  - [ ] 12.4 Accept addition of `.vscode/launch.json` from main
  - [ ] 12.5 Handle `.vscode/settings.json` merge if needed
  - [ ] 12.6 Delete outdated `PROJECT_OVERVIEW.md` from root if present
  - [ ] 12.7 Accept addition of `run-server.ps1` from main
  - [ ] 12.8 Keep task files from feature branch in `/tasks/` directory

- [ ] 13.0 Verify Conflict Resolution Complete
  - [ ] 13.1 Run `git status` to check for remaining conflicts
  - [ ] 13.2 Search all files for conflict markers: `git grep -n "<<<<<<< HEAD"`
  - [ ] 13.3 Search all files for conflict markers: `git grep -n "======="`
  - [ ] 13.4 Search all files for conflict markers: `git grep -n ">>>>>>> feature"`
  - [ ] 13.5 Manually inspect any suspected files
  - [ ] 13.6 Ensure no "CONFLICT" messages remain in git status

- [ ] 14.0 Build and Compilation Verification
  - [ ] 14.1 Clean previous builds: `mvn clean`
  - [ ] 14.2 Compile the project: `mvn compile`
  - [ ] 14.3 Review compilation output for errors
  - [ ] 14.4 If errors exist, identify the file and line number
  - [ ] 14.5 Fix any import errors (missing classes, incorrect packages)
  - [ ] 14.6 Fix any syntax errors remaining from merge
  - [ ] 14.7 Re-run `mvn compile` until successful
  - [ ] 14.8 Run `mvn package` to create JAR file
  - [ ] 14.9 Verify target/ directory contains compiled classes

- [ ] 15.0 Database Schema Verification
  - [ ] 15.1 Review `Project Principle/demo/database-setup.sql`
  - [ ] 15.2 Ensure customers table definition exists
  - [ ] 15.3 Ensure cart_items table definition exists
  - [ ] 15.4 Check if employees, managers, menu_items, users tables are needed (based on main branch entities)
  - [ ] 15.5 If missing, add table definitions for Employee, Manager, MenuItem, User entities
  - [ ] 15.6 Verify foreign key relationships match entity @ManyToOne/@OneToMany annotations
  - [ ] 15.7 Update database-setup.sql with complete schema if needed

- [ ] 16.0 Application Startup Testing
  - [ ] 16.1 Ensure MySQL is running on localhost:3306
  - [ ] 16.2 Ensure database `restaurant_db` exists
  - [ ] 16.3 Run application: `mvn spring-boot:run`
  - [ ] 16.4 Monitor console for startup errors
  - [ ] 16.5 Verify all entity mappings are recognized (no "Unknown entity" errors)
  - [ ] 16.6 Check for port conflicts (default 8080)
  - [ ] 16.7 Confirm application starts without exceptions
  - [ ] 16.8 Access landing page: http://localhost:8080
  - [ ] 16.9 Verify page loads successfully

- [ ] 17.0 Functional Testing - Customer Features
  - [ ] 17.1 Test customer registration: `/register`
  - [ ] 17.2 Verify validation errors appear for invalid input
  - [ ] 17.3 Test successful registration creates customer in database
  - [ ] 17.4 Test customer login: `/login`
  - [ ] 17.5 Verify session is created on successful login
  - [ ] 17.6 Test cart operations: Add item via API
  - [ ] 17.7 Test cart operations: Update quantity
  - [ ] 17.8 Test cart operations: Remove item
  - [ ] 17.9 Test cart operations: Get cart total
  - [ ] 17.10 Verify ownership validation (can't access other customer's cart)

- [ ] 18.0 Functional Testing - Employee Features (if implemented in UI)
  - [ ] 18.1 Locate employee login interface
  - [ ] 18.2 Test employee login with 6-digit code
  - [ ] 18.3 Verify employee can access employee-specific pages
  - [ ] 18.4 Test employee logout

- [ ] 19.0 Functional Testing - Manager Features
  - [ ] 19.1 Access manager dashboard: `/manager`
  - [ ] 19.2 Test menu item creation (CRUD operations)
  - [ ] 19.3 Test menu item update
  - [ ] 19.4 Test menu item deletion
  - [ ] 19.5 Verify manager can view sales reports (if implemented)
  - [ ] 19.6 Test any other manager-specific features

- [ ] 20.0 Code Quality and Cleanup
  - [ ] 20.1 Remove any commented-out code from merge conflicts
  - [ ] 20.2 Remove any TODO comments added during merge
  - [ ] 20.3 Ensure consistent code formatting (indentation, spacing)
  - [ ] 20.4 Remove unused imports in all modified files
  - [ ] 20.5 Verify all public methods have proper Javadoc (optional but recommended)
  - [ ] 20.6 Check for any hardcoded values that should be in configuration
  - [ ] 20.7 Run static analysis if available: `mvn checkstyle:check` (if configured)

- [ ] 21.0 Security Verification
  - [ ] 21.1 Review SecurityConfig.java for any merge conflicts
  - [ ] 21.2 Ensure customer authentication endpoints are properly secured
  - [ ] 21.3 Ensure employee authentication endpoints are properly secured
  - [ ] 21.4 Verify cart endpoints require authentication
  - [ ] 21.5 Test CORS configuration allows frontend to call APIs
  - [ ] 21.6 Verify CSRF protection is properly configured
  - [ ] 21.7 Test that unauthenticated users can't access protected resources

- [ ] 22.0 Documentation Updates
  - [ ] 22.1 Update README.md with any new setup instructions
  - [ ] 22.2 Regenerate PROJECT_OVERVIEW.md with merged feature set
  - [ ] 22.3 Document any manual database migrations needed
  - [ ] 22.4 Add notes about Employee/Manager setup if required
  - [ ] 22.5 Update any API documentation (if exists)
  - [ ] 22.6 Document merge decisions in commit message

- [ ] 23.0 Commit the Merge
  - [ ] 23.1 Review all staged changes: `git diff --cached`
  - [ ] 23.2 Verify file count matches expectations
  - [ ] 23.3 Write comprehensive commit message documenting merge
  - [ ] 23.4 Include notes on conflict resolution decisions
  - [ ] 23.5 Commit the merge: `git commit`
  - [ ] 23.6 Tag the merge commit: `git tag merge-customer-cart-v1.0`
  - [ ] 23.7 Push to remote: `git push origin main`
  - [ ] 23.8 Push tags: `git push origin --tags`

- [ ] 24.0 Post-Merge Cleanup
  - [ ] 24.1 Delete feature branch locally if no longer needed: `git branch -d feature/seperate-customer-cart`
  - [ ] 24.2 (Optional) Delete remote feature branch: `git push origin --delete feature/seperate-customer-cart`
  - [ ] 24.3 Delete backup branch if merge is successful: `git branch -D backup-main-before-merge`
  - [ ] 24.4 Update project board or issue tracker to mark merge complete
  - [ ] 24.5 Notify team members of successful merge
  - [ ] 24.6 Schedule follow-up testing session
  - [ ] 24.7 Monitor application logs for any post-merge issues

---

## Rollback Plan (If Needed)

If critical issues are discovered after merge:

1. **Immediate Rollback:**
   ```bash
   git reset --hard backup-main-before-merge
   git push origin main --force  # ⚠️ Coordinate with team first!
   ```

2. **Selective Rollback:**
   ```bash
   git revert <merge-commit-hash>
   git push origin main
   ```

3. **Cherry-pick Approach:**
   - Reset to backup
   - Cherry-pick specific commits that work
   - Re-test incrementally

---

## Success Criteria Checklist

- [ ] ✅ Application compiles without errors
- [ ] ✅ Application starts without exceptions  
- [ ] ✅ Customer registration works
- [ ] ✅ Customer login works
- [ ] ✅ Cart CRUD operations work
- [ ] ✅ Employee login works
- [ ] ✅ Manager dashboard loads
- [ ] ✅ Menu management works
- [ ] ✅ No conflict markers in any file
- [ ] ✅ Database schema supports all entities
- [ ] ✅ All tests pass (if any exist)
- [ ] ✅ Security configuration protects all endpoints
- [ ] ✅ CORS allows frontend access
- [ ] ✅ Session management works correctly

---

**Task List Status:** ✅ Ready for Execution  
**Total Tasks:** 24 major tasks, 190+ sub-tasks  
**Estimated Time:** 4-6 hours (depending on conflict complexity)  
**Created:** October 2, 2025  
**AI Assistant:** GitHub Copilot
