export function apiError(error) {
  return error.response?.data?.message || error.response?.data?.errors?.general || error.message || 'An unexpected error occurred';
}
