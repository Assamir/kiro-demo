# User Manual

Complete guide for using the Insurance Backoffice System.

## 🚀 Getting Started

### System Access
1. Open your web browser
2. Navigate to: http://localhost:3000
3. Use your login credentials to access the system

### Login Credentials
See [Users and Passwords](../deployment/users-and-passwords.md) for complete credential list.

**Quick Access:**
- **Admin**: admin@insurance.com / admin123
- **Operator**: mike.johnson@insurance.com / password123

## 🏠 Dashboard Overview

After logging in, you'll see the main dashboard with:

### Navigation Menu
- **Dashboard** - System overview and statistics
- **Policies** - Manage insurance policies
- **Users** - User management (Admin only)
- **Profile** - Your account settings

### Dashboard Widgets
- **Total Policies** - Count of all policies in system
- **Active Policies** - Currently active policies
- **Expiring Soon** - Policies expiring within 30 days
- **Recent Activity** - Latest system activities

## 📋 Policy Management

### Viewing Policies
1. Click **Policies** in the navigation menu
2. The policies list shows:
   - Policy Number
   - Client Name
   - Vehicle Registration
   - Insurance Type (OC/AC/NNW)
   - Start/End Dates
   - Premium Amount
   - Status

### Searching and Filtering

**Search Options:**
- Policy Number
- Client Name
- Vehicle Registration
- Insurance Type
- Status

**Filter by Date Range:**
- Start Date
- End Date
- Creation Date

### Creating a New Policy

1. Click **"New Policy"** button
2. Fill in the policy form:

#### Client Information
- Select existing client from dropdown
- Or create new client if needed

#### Vehicle Information
- Select existing vehicle from dropdown
- Or register new vehicle if needed

#### Policy Details
- **Insurance Type**: Choose OC, AC, or NNW
- **Start Date**: Policy effective date
- **End Date**: Policy expiration date
- **Discount/Surcharge**: Additional amount (optional)

3. Click **"Calculate Premium"** to see estimated cost
4. Click **"Create Policy"** to save

### Editing a Policy
1. Find the policy in the list
2. Click the **Edit** button (pencil icon)
3. Modify the required fields
4. Click **"Update Policy"** to save changes

**Note**: Some fields may be restricted based on policy status and your role.

### Canceling a Policy
1. Find the policy in the list
2. Click the **Cancel** button (X icon)
3. Confirm the cancellation
4. The policy status will change to "CANCELED"

### Generating Policy Documents
1. Find the policy in the list
2. Click the **PDF** button (document icon)
3. The policy document will be generated and downloaded

## 👥 User Management (Admin Only)

### Viewing Users
1. Click **Users** in the navigation menu
2. The user list shows:
   - Full Name
   - Email Address
   - Role (Admin/Operator)
   - Last Login
   - Status

### Creating a New User
1. Click **"New User"** button
2. Fill in the user form:
   - **Full Name**: User's display name
   - **Email**: Login email (must be unique)
   - **Password**: Initial password
   - **Role**: ADMIN or OPERATOR
3. Click **"Create User"** to save

### Editing User Information
1. Find the user in the list
2. Click the **Edit** button
3. Modify the required fields
4. Click **"Update User"** to save

**Note**: Users cannot edit their own role.

### Deactivating Users
1. Find the user in the list
2. Click the **Deactivate** button
3. Confirm the action
4. The user will no longer be able to log in

## 🔐 User Roles and Permissions

### Administrator (ADMIN)
**Can do everything:**
- ✅ View all policies
- ✅ Create, edit, cancel policies
- ✅ Generate policy documents
- ✅ Manage users (create, edit, deactivate)
- ✅ View system statistics
- ✅ Access all system features

### Operator (OPERATOR)
**Limited access:**
- ✅ View all policies
- ✅ Create, edit, cancel policies
- ✅ Generate policy documents
- ✅ View basic statistics
- ❌ Cannot manage users
- ❌ Cannot access admin settings

## 📊 Insurance Types

### OC (Obligatory Civil Liability)
- **Required by law** for all vehicles
- Covers damage to third parties
- Lower premium rates
- Basic coverage

### AC (Auto Casco)
- **Optional comprehensive** coverage
- Covers own vehicle damage
- Higher premium rates
- Extensive coverage options

### NNW (Accidents of Uninsured Drivers)
- **Optional additional** coverage
- Covers accidents with uninsured drivers
- Moderate premium rates
- Supplementary protection

## 🔍 Search and Navigation Tips

### Quick Search
- Use the search box at the top of any list
- Search works across multiple fields
- Results update as you type

### Keyboard Shortcuts
- **Ctrl + F**: Focus search box
- **Ctrl + N**: Create new item (where applicable)
- **Esc**: Close dialogs/modals
- **Enter**: Submit forms

### Pagination
- Use page numbers at the bottom of lists
- Change items per page (10, 25, 50, 100)
- Jump to first/last page quickly

## 📱 Mobile Usage

The system is responsive and works on mobile devices:

### Mobile Navigation
- Tap the menu icon (☰) to open navigation
- Swipe left/right on tables to scroll
- Use pinch-to-zoom for detailed views

### Mobile Limitations
- Some advanced features work better on desktop
- PDF generation may require desktop browser
- Large data exports recommended on desktop

## 🔧 Troubleshooting

### Common Issues

#### Cannot Login
1. Check your email and password
2. Ensure Caps Lock is off
3. Try refreshing the page
4. Contact your administrator

#### Policies Not Loading
1. Check your internet connection
2. Refresh the page (F5)
3. Clear browser cache
4. Try a different browser

#### PDF Generation Fails
1. Ensure pop-ups are allowed
2. Check browser's download settings
3. Try a different browser
4. Contact support if issue persists

#### Slow Performance
1. Close unnecessary browser tabs
2. Clear browser cache and cookies
3. Check internet connection speed
4. Try during off-peak hours

### Browser Compatibility

**Recommended Browsers:**
- ✅ Chrome 90+
- ✅ Firefox 88+
- ✅ Safari 14+
- ✅ Edge 90+

**Not Supported:**
- ❌ Internet Explorer
- ❌ Very old browser versions

## 📞 Getting Help

### Self-Help Resources
1. Check this user manual
2. Review [Troubleshooting Guide](troubleshooting.md)
3. Check [API Documentation](../api/endpoints.md)

### Contact Support
- **Email**: support@insurance-system.com
- **Phone**: +48 123 456 789
- **Hours**: Monday-Friday, 9:00-17:00 CET

### Reporting Issues
When reporting issues, please include:
- Your username (not password)
- Browser and version
- Steps to reproduce the problem
- Error messages (if any)
- Screenshots (if helpful)

---

**Quick Reference:**
- [Users and Passwords](../deployment/users-and-passwords.md)
- [Troubleshooting Guide](troubleshooting.md)
- [Admin Guide](admin-guide.md)