import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { api } from "./api";
import type { PlaybackDto, PostDetailsDto } from "./api";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import "./VideoShowcase.css";

interface ChatMessage {
  postId: number;
  username: string;
  content: string;
  timestamp: string;
}

export default function VideoDetails() {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const didInitialSeekRef = useRef(false);
  const [liveMessageText, setLiveMessageText] = useState("");

  const clientRef = useRef<Client | null>(null);

  const { id } = useParams();
  const videoRef = useRef<HTMLVideoElement | null>(null);

  const [post, setPost] = useState<PostDetailsDto | null>(null);
  const [playback, setPlayback] = useState<PlaybackDto | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [commentText, setCommentText] = useState("");

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
    didInitialSeekRef.current = false;
  }, [id]);
  const hasRegisteredView = useRef(false);

  useEffect(() => {
    if (!id || hasRegisteredView.current) return;

    hasRegisteredView.current = true;

    const init = async () => {
      try {
        await api.registerView(id);
        await load();
        await loadPlayback();
      } catch (e) {
        if (e instanceof Error) setError(e.message);
      }
    };

    init();
  }, [id]);

  const sendLiveMessage = () => {
    if (!clientRef.current || !id) return;
    if (!liveMessageText.trim()) return;

    if (clientRef.current.connected) {
      clientRef.current.publish({
        destination: `/app/chat/${id}`,
        body: JSON.stringify({
          postId: Number(id),
          content: liveMessageText.trim(),
        }),
      });

      setLiveMessageText("");
    } else {
      console.warn("STOMP client nije još povezan");
    }
  };

  useEffect(() => {
    if (!id) return;

    let timer: number | null = null;

    const init = async () => {
      try {
        await load();
        const pb = await loadPlayback();

        if (pb && !pb.available) {
          timer = window.setInterval(async () => {
            try {
              const next = await loadPlayback();
              if (next.available) {
                if (timer) window.clearInterval(timer);
                timer = null;
              }
            } catch (e: unknown) {
              if (e instanceof Error) {
                setError(e.message);
              }
            }
          }, 1000);
        }
      } catch (e: unknown) {
        if (e instanceof Error) {
          setError(e.message);
        }
      }
    };

    init();

    return () => {
      if (timer) window.clearInterval(timer);
    };
  }, [id, load, loadPlayback]);

  useEffect(() => {
    if (!id) return;

    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      reconnectDelay: 5000,
    });

    client.onConnect = () => {
      client.subscribe(`/topic/chat/${id}`, (msg) => {
        const body: ChatMessage = JSON.parse(msg.body);
        setMessages((prev) => [...prev, body]);
      });
    };

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [id]);

  const like = async () => {
    if (!id) return;
    try {
      await api.likePost(id);
      await load();
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (e: any) {
      setError(e.message);
    }
  };

  if (error) return <p style={{ color: "salmon" }}>{error}</p>;
  if (!post) return <p>Učitavanje...</p>;

  const isAvailable = playback?.available ?? post.available;

  return (
    <div className="container">
      <div className="box1">
        <h1>{post.title}</h1>

        {!isAvailable && (
          <div
            style={{
              padding: 12,
              border: "1px solid #444",
              borderRadius: 12,
              maxWidth: 900,
            }}
          >
            <p>Video je zakazan i još nije dostupan.</p>
            <p>
              Zakazano vreme: {post.scheduledAt ?? playback?.scheduledAt ?? "?"}
            </p>
          </div>
        )}

        {isAvailable && (
          <video
            ref={videoRef}
            controls
            style={{ width: "100%", maxWidth: 900 }}
            onLoadedMetadata={() => {
              if (didInitialSeekRef.current) return;
              const offset = playback?.startOffsetSec ?? 0;

              if (videoRef.current) {
                videoRef.current.currentTime = offset;
                videoRef.current.play().catch(() => {});
                didInitialSeekRef.current = true;
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
            style={{
              padding: "10px",
              borderRadius: 8,
              border: "1px solid #555",
            }}
          />
          <button type="submit" disabled={!commentText.trim()}>
            Komentariši
          </button>
        </form>
      </div>
      {post.scheduledAt && (
        <div className="box2">
          <h3>Live Chat</h3>

          <div
            style={{
              maxWidth: 900,
              height: 300,
              overflowY: "auto",
              border: "1px solid #444",
              padding: 12,
              borderRadius: 8,
            }}
          >
            {messages.map((m, i) => (
              <div key={i} style={{ marginBottom: 8 }}>
                <strong>{m.username}</strong>: {m.content}
              </div>
            ))}
          </div>
          <form
            onSubmit={(e) => {
              e.preventDefault();
              sendLiveMessage();
            }}
            style={{
              display: "flex",
              gap: 8,
              maxWidth: 900,
              marginTop: 8,
            }}
          >
            <input
              type="text"
              placeholder="Pošalji poruku u live chat..."
              value={liveMessageText}
              onChange={(e) => setLiveMessageText(e.target.value)}
              style={{
                flex: 1,
                padding: "10px",
                borderRadius: 8,
                border: "1px solid #555",
              }}
            />
            <button type="submit" disabled={!liveMessageText.trim()}>
              Pošalji
            </button>
          </form>
        </div>
      )}
    </div>
  );
}
