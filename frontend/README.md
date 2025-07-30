# Insurance Backoffice Frontend

This is the React TypeScript frontend for the Insurance Backoffice System.

## Features

- **React 18** with TypeScript
- **Material-UI (MUI)** for UI components
- **React Router** for navigation and protected routes
- **Custom theme** with light colors, blue and gold accents
- **Role-based access control** (Admin/Operator)
- **JWT authentication** with automatic token management
- **Responsive design** for desktop and mobile

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── common/         # Common components (ProtectedRoute, etc.)
│   └── layout/         # Layout components (Header, Sidebar, etc.)
├── contexts/           # React contexts (AuthContext)
├── pages/              # Page components
├── services/           # API services and HTTP client
├── theme/              # Material-UI theme configuration
├── types/              # TypeScript type definitions
└── utils/              # Utility functions and constants
```

## Available Scripts

- `npm start` - Runs the app in development mode
- `npm build` - Builds the app for production
- `npm test` - Launches the test runner
- `npm run eject` - Ejects from Create React App (not recommended)

## Environment Variables

Create a `.env` file in the frontend directory:

```
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

## Authentication

The application uses JWT tokens for authentication. Tokens are automatically:
- Stored in localStorage upon login
- Attached to API requests via interceptors
- Removed on logout or when expired (401 responses)

## Routing

- `/login` - Public login page
- `/dashboard` - Protected dashboard (all authenticated users)
- `/policies` - Protected policies page (all authenticated users)
- `/users` - Protected user management (Admin only)

## Theme

The application uses a custom Material-UI theme with:
- Light color scheme
- Blue primary color (#1976d2)
- Gold secondary/accent color (#ffd700)
- Custom component styling for consistent branding