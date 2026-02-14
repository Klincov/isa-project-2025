import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { api } from "./api";
import type { PlaybackDto, PostDetailsDto } from "./api";

export default function VideoDetails() {
  const { id } = useParams();
  const videoRef = useRef<HTMLVideoElement | null>(null);

  const [post, setPost] = useState<PostDetailsDto | null>(null);
  const [playback, setPlayback] = useState<PlaybackDto | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [commentText, setCommentText] = useState("");

  const [didInitialSeek, setDidInitialSeek] = useState(false);

  const load = async () => {
    if (!id) return;
    const p = await api.getPost(id);
    setPost(p);
  };

  const loadPlayback = async (): Promise<PlaybackDto> => {
  if (!id) throw new Error("Missing id");
  const pb = await api.getPlayback(id);
  setPlayback(pb);
  return pb;
};


  useEffect(() => {
    if (!id) return;

    let timer: number | null = null;
    setDidInitialSeek(false);

    (async () => {
      await api.registerView(id);
      await load();
      const pb = await loadPlayback();

      if (pb && !pb.available) {
        timer = window.setInterval(async () => {
          try {
            const next = await loadPlayback();
            if (next.available) {
              if (timer) window.clearInterval(timer);
              timer = null;
              setDidInitialSeek(false);
            }
          } catch (e: any) {
            setError(e.message);
          }
        }, 1000);
      }
    })().catch((e: any) => {
  console.log("VideoDetails error object:", e);
  setError(String(e?.message ?? e));
});


    return () => {
      if (timer) window.clearInterval(timer);
    };
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

  const isAvailable = playback?.available ?? post.available;

  return (
    <div>
      <h1>{post.title}</h1>

      {!isAvailable && (
        <div style={{ padding: 12, border: "1px solid #444", borderRadius: 12, maxWidth: 900 }}>
          <p>Video je zakazan i još nije dostupan.</p>
          <p>Zakazano vreme: {post.scheduledAt ?? playback?.scheduledAt ?? "?"}</p>
        </div>
      )}

      {isAvailable && (
        <video
          ref={videoRef}
          controls
          style={{ width: "100%", maxWidth: 900 }}
          onLoadedMetadata={() => {
            if (didInitialSeek) return;
            const offset = playback?.startOffsetSec ?? 0;

            if (videoRef.current) {
              videoRef.current.currentTime = offset;
              videoRef.current.play().catch(() => {});
              setDidInitialSeek(true);
            }
          }}
        >
          <source
            src={`${import.meta.env.VITE_API_URL}${post.videoUrl}`}
            type="video/mp4"
          />
        </video>
      )}

      <div style={{ display: "flex", gap: 12, marginTop: 12 }}>
        <button onClick={like}>Like ({post.likesCount})</button>
      </div>

      <p>Views: {post.viewCount}</p>

      <h3>Opis</h3>
      <p>{post.description}</p>

      <h3>Komentari</h3>
      <form
        onSubmit={(e) => {
          e.preventDefault();
          setCommentText("");
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
