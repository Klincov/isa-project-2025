import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { api } from "./api";
import type { PostDetailsDto } from "./api";


export default function VideoDetails() {
  const { id } = useParams();
  const [post, setPost] = useState<PostDetailsDto | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [commentText, setCommentText] = useState("");


  const load = async () => {
    if (!id) return;
    const p = await api.getPost(id);
    setPost(p);
  };

  useEffect(() => {
    load().catch((e) => setError(e.message));
  }, [id]);

  const like = async () => {
    if (!id) return;
    try {
      await api.likePost(id);
      await load();
    } catch (e: any) {
      setError(e.message);
    }
  };

  if (error) return <p style={{ color: "salmon" }}>{error}</p>;
  if (!post) return <p>Učitavanje...</p>;

  return (
    <div>
      <h1>{post.title}</h1>

      <video controls style={{ width: "100%", maxWidth: 900 }}>
        <source src={`${import.meta.env.VITE_API_URL}${post.videoUrl}`} type="video/mp4" />
      </video>

      <div style={{ display: "flex", gap: 12, marginTop: 12 }}>
        <button onClick={like}>Like ({post.likesCount})</button>
      </div>

      <h3>Opis</h3>
      <p>{post.description}</p>

      <h3>Komentari</h3>

<form
  onSubmit={(e) => {
    e.preventDefault(); // da ne reloaduje stranicu
    setCommentText(""); // opciono: očisti polje
  }}
  style={{ display: "grid", gap: 12, maxWidth: 900, marginTop: 12 }}
>
  <textarea
    rows={4}
    placeholder="Napiši komentar..."
    value={commentText}
    onChange={(e) => setCommentText(e.target.value)}
    style={{ padding: "10px", borderRadius: 8, border: "1px solid #555" }}
  />

  <button type="submit" disabled={!commentText.trim()}>
    Komentariši
  </button>
</form>

    </div>
  );
}
