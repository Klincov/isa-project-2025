const API_URL = import.meta.env.VITE_API_URL as string;

async function request<T>(path: string, options: RequestInit): Promise<T> {
  const res = await fetch(`${API_URL}${path}`, {
    credentials: "include",
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
  });

  const text = await res.text();
  let data: any = null;
  try { data = text ? JSON.parse(text) : null; } catch {}

  if (!res.ok) {
    throw new Error(data?.message || text || `HTTP ${res.status}`);
  }
  return data as T;
}


export type ApiMessage = { message: string };

export type RegisterRequest = {
  email: string;
  username: string;
  password: string;
  confirmPassword: string;
  firstName: string;
  lastName: string;
  address: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type PostListItemDto = {
  id: number;
  title: string;
  thumbnailUrl: string;
  createdAt: string;
};

export type PostDetailsDto = {
  id: number;
  title: string;
  description: string;
  tags: string[];
  videoUrl: string;
  thumbnailUrl: string;
  likesCount: number;
  latitude: number | null;
  longitude: number | null;
  createdAt: string;
};

export const api = {
  register: (body: RegisterRequest) =>
    request<ApiMessage>("/api/auth/register", { method: "POST", body: JSON.stringify(body) }),
  login: (body: LoginRequest) =>
    request<ApiMessage>("/api/auth/login", { method: "POST", body: JSON.stringify(body) }),

  listPosts: () => request<PostListItemDto[]>("/api/posts", { method: "GET" }),
  getPost: (id: string) => request<PostDetailsDto>(`/api/posts/${id}`, { method: "GET" }),
  likePost: (id: string) => request<ApiMessage>(`/api/posts/${id}/like`, { method: "POST" }),
};



