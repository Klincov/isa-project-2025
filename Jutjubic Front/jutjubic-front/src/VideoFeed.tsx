import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "./api";
import type { PostListItemDto } from "./api";

type TrendingItem = { rank: number; postId: number; score: number };

type TrendingCard = {
  id: number;
  title: string;
  thumbnailUrl: string;
};

export default function VideoFeed() {
  const [items, setItems] = useState<PostListItemDto[]>([]);
  const [trending, setTrending] = useState<TrendingCard[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        setError(null);

        const latest = await api.listPosts();
        if (cancelled) return;
        setItems(latest);

        const top3: TrendingItem[] = await api.getPopularTop3();

        const byId = new Map<number, PostListItemDto>(latest.map((p) => [p.id, p]));

        const missingIds = top3.map((t) => t.postId).filter((id) => !byId.has(id));

        const missingDetails =
          missingIds.length === 0
            ? []
            : await Promise.all(missingIds.map((id) => api.getPostDetails(id)));

        const detailsById = new Map<number, TrendingCard>(
          missingDetails.map((d) => [
            d.id,
            { id: d.id, title: d.title, thumbnailUrl: d.thumbnailUrl },
          ])
        );

        const cards: TrendingCard[] = top3
          .map((t) => {
            const p = byId.get(t.postId);
            if (p) return { id: p.id, title: p.title, thumbnailUrl: p.thumbnailUrl };
            const d = detailsById.get(t.postId);
            return d ?? null;
          })
          .filter((x): x is TrendingCard => Boolean(x));

        if (cancelled) return;
        setTrending(cards);
      } catch (e: any) {
        if (cancelled) return;
        setError(e?.message ?? "Greška");
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div>
      {/* TRENDING */}
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>Trending</h2>

        {trending.length === 0 ? (
          <p style={{ color: "#bbb", marginTop: 8 }}>
            Trenutno nema trending videa (ili ETL još nije pokrenut).
          </p>
        ) : (
          <div
            style={{
              display: "grid",
              gap: 16,
              gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))",
              marginTop: 16,
            }}
          >
            {trending.map((v) => (
              <Link key={v.id} to={`/video/${v.id}`} style={{ textDecoration: "none" }}>
                <div style={{ border: "1px solid #666", borderRadius: 12, padding: 12 }}>
                  <img
                    src={`${import.meta.env.VITE_API_URL}${v.thumbnailUrl}`}
                    alt={v.title}
                    style={{ width: "100%", borderRadius: 8, display: "block" }}
                  />
                  <h3 style={{ marginTop: 10 }}>{v.title}</h3>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>

      {/* NAJNOVIJI */}
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          gap: 12,
          marginBottom: 16,
        }}
      >
        <h2 style={{ margin: 0 }}>Najnoviji videi</h2>

        <Link to="/create-post">
          <button type="button">Dodaj video</button>
        </Link>
      </div>

      {error && <p style={{ color: "salmon", textAlign: "center" }}>Greška pri dobavljanju videa.</p>}

      <div
        style={{
          display: "grid",
          gap: 16,
          gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))",
          marginTop: 16,
        }}
      >
        {items.map((v) => (
          <Link key={v.id} to={`/video/${v.id}`} style={{ textDecoration: "none" }}>
            <div style={{ border: "1px solid #444", borderRadius: 12, padding: 12 }}>
              <img
                src={`${import.meta.env.VITE_API_URL}${v.thumbnailUrl}`}
                alt={v.title}
                style={{ width: "100%", borderRadius: 8, display: "block" }}
              />
              <h3 style={{ marginTop: 10 }}>{v.title}</h3>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
