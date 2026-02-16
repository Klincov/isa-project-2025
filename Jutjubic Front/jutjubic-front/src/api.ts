const API_URL = import.meta.env.VITE_API_URL as string;

function getCookie(name: string): string | null {
  return document.cookie
    .split("; ")
    .find(row => row.startsWith(name + "="))
    ?.split("=")[1] ?? null;
}

async function request<T>(path: string, options: RequestInit): Promise<T> {
  const csrfToken = getCookie("XSRF-TOKEN");
  const res = await fetch(`${API_URL}${path}`, {
    credentials: "include",
    ...options,
    headers: {
  ...(options.body ? { "Content-Type": "application/json" } : {}),
  ...(csrfToken ? { "X-XSRF-TOKEN": csrfToken } : {}),
  ...(options.headers || {}),
},

  });

  const text = await res.text();
  let data: any = null;
  try { data = text ? JSON.parse(text) : null; } catch {}

  if (!res.ok) {
  const msg = (data && typeof data === "object" && "message" in data) ? (data as any).message : null;
  throw new Error(msg || text || `HTTP ${res.status}`);
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
  viewCount: number;
  scheduledAt: string | null;
  available: boolean;
};

export type PlaybackDto = {
  available: boolean;
  serverNow: string;
  scheduledAt: string | null;
  startOffsetSec: number;
};

export type PopularVideoItemDto = {
  rank: number;
  postId: number;
  score: number;
};

export const api = {
  register: (body: RegisterRequest) =>
    request<ApiMessage>("/api/auth/register", { method: "POST", body: JSON.stringify(body) }),
  login: (body: LoginRequest) =>
    request<ApiMessage>("/api/auth/login", { method: "POST", body: JSON.stringify(body) }),

  listPosts: () => request<PostListItemDto[]>("/api/posts", { method: "GET" }),
  getPost: (id: string) => request<PostDetailsDto>(`/api/posts/${id}`, { method: "GET" }),
  likePost: (id: string) => request<ApiMessage>(`/api/posts/${id}/like`, { method: "POST" }),
  registerView: async (id: string) => {await fetch(`${API_URL}/api/posts/${id}/view`, { method: "POST", credentials: "include" });},
  getPlayback: (id: string) => request<PlaybackDto>(`/api/posts/${id}/playback`, { method: "GET" }),
  getPopularTop3: () => request<PopularVideoItemDto[]>("/api/home/popular", { method: "GET" }),
  getPostDetails: (id: number) => request<PostDetailsDto>(`/api/posts/${id}`, { method: "GET" }),

};



