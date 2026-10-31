# Integration plan (not active in this starter)

| Platform | Intended official route | Current status |
|---|---|---|
| Fiverr | Verify available official API / partner access for intended actions; otherwise use manual workflow | Not connected |
| Instagram | Meta Instagram Platform / Graph API; eligible Professional account and approved permissions | Not connected |
| Facebook | Meta Graph API for Pages and approved publishing scopes | Not connected |
| Gmail | Google OAuth 2.0 + Gmail API scopes | Not connected |
| LinkedIn | LinkedIn developer product + approved OAuth scopes for member/organization posting | Not connected |
| WhatsApp | Excluded by user request | Excluded |

No credentials are embedded in the Android app. OAuth tokens must never be shipped in source code or stored as plaintext in the APK. The production design should use a backend token vault, short-lived app sessions, per-action approval, and revocation.
