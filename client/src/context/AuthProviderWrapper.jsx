import { Auth0Provider } from '@auth0/auth0-react'

const auth0Domain = import.meta.env.VITE_AUTH0_DOMAIN || 'example.auth0.com'
const auth0ClientId = import.meta.env.VITE_AUTH0_CLIENT_ID || 'placeholder-client-id'
const auth0Audience = import.meta.env.VITE_AUTH0_AUDIENCE || ''

function AuthProviderWrapper({ children }) {
  const authorizationParams = {
    redirect_uri: `${window.location.origin}/`,
  }

  if (auth0Audience) {
    authorizationParams.audience = auth0Audience
  }

  return (
    <Auth0Provider
      domain={auth0Domain}
      clientId={auth0ClientId}
      authorizationParams={authorizationParams}
      cacheLocation="localstorage"
      useRefreshTokens
    >
      {children}
    </Auth0Provider>
  )
}

export default AuthProviderWrapper
