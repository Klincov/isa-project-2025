import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "./api";
import type { PostListItemDto } from "./api";

export default function VideoFeed() {
  const [items, setItems] = useState<PostListItemDto[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.listPosts()
      .then(setItems)
      .catch((e) => setError(e.message));
  }, []);

  return (
    <div>
      <div style={{display: "flex",alignItems: "center",justifyContent: "space-between",gap: 12,marginBottom: 16,}}>
    <h2 style={{ margin: 0 }}>Najnoviji videi</h2>

    <Link to="/create-post">
    <button type="button"> Dodaj video</button>
    </Link>
    </div>
      {error && <p style={{ color: "salmon", textAlign:"center" }}>Greška pri dobavljanju videa.</p>}

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
