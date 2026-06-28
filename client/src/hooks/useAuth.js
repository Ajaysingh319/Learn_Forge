function useAuth() {
  return {
    isAuthenticated: false,
    user: null,
    login: () => {},
    logout: () => {},
  }
}

export default useAuth
