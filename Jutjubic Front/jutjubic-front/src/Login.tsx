import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "./api";

const Login = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setInfo(null);
    setLoading(true);

    try {
      const res = await api.login({ email, password });
      setInfo(res.message);
      navigate("/");
    } catch (err: any) {
      setError(err?.message ?? "Greška.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1>Prijava</h1>

      <form onSubmit={onSubmit} style={{ display: "grid", gap: "12px", minWidth: 320 }}>
        <input
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          style={{ padding: "10px", borderRadius: 8, border: "1px solid #555" }}
        />

        <input
          placeholder="Lozinka"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          style={{ padding: "10px", borderRadius: 8, border: "1px solid #555" }}
        />

        <button type="submit" disabled={loading}>
          {loading ? "Prijavljivanje..." : "Prijavi se"}
        </button>

        {error && <div style={{ color: "salmon" }}>{error}</div>}
        {info && <div style={{ color: "lightgreen" }}>{info}</div>}

        <div style={{ textAlign: "center" }}>
          Nemaš nalog? <Link to="/register">Registruj se</Link>
        </div>
      </form>
    </div>
  );
};

export default Login;
