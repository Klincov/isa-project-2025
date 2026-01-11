import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "./api";

const Register = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
    firstName: "",
    lastName: "",
    address: "",
  });

  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const set = (key: string, value: string) => setForm((p) => ({ ...p, [key]: value }));

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setInfo(null);
    setLoading(true);

    try {
      const res = await api.register(form);
      setInfo(res.message);
      setTimeout(() => navigate("/login"), 700);
    } catch (err: any) {
      console.log(err);
      setError(err?.message ?? "Greška.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1 style={{textAlign:"center"}}>Registracija</h1>

      <form onSubmit={onSubmit} style={{ display: "grid", gap: "12px", minWidth: 320 }}>
        <input placeholder="Email" value={form.email} onChange={(e) => set("email", e.target.value)}
          style={{ padding: "10px", borderRadius: 8, border: "1px solid #555" }} />
        <input placeholder="Korisničko ime" value={form.username} onChange={(e) => set("username", e.target.value)}
          style={{ padding: "10px", borderRadius: 8, border: "1px solid #555" }} />

        <input placeholder="Lozinka" type="password" value={form.password} onChange={(e) => set("password", e.target.value)}
          style={{ padding: "10px", borderRadius: 8, border: "1px solid #555" }} />
        <input placeholder="Potvrdi lozinku" type="password" value={form.confirmPassword} onChange={(e) => set("confirmPassword", e.target.value)}
          style={{ padding: "10px", borderRadius: 8, border: "1px solid #555" }} />

        <input placeholder="Ime" value={form.firstName} onChange={(e) => set("firstName", e.target.value)}
          style={{ padding: "10px", borderRadius: 8, border: "1px solid #555" }} />
        <input placeholder="Prezime" value={form.lastName} onChange={(e) => set("lastName", e.target.value)}
          style={{ padding: "10px", borderRadius: 8, border: "1px solid #555" }} />
        <input placeholder="Adresa" value={form.address} onChange={(e) => set("address", e.target.value)}
          style={{ padding: "10px", borderRadius: 8, border: "1px solid #555" }} />

        <button type="submit" disabled={loading}>
          {loading ? "Slanje..." : "Registruj se"}
        </button>

        {error && <div style={{ textAlign:"center",color: "salmon" }}>Greška pri komuniciranju sa serverom</div>}
        {info && <div style={{ color: "lightgreen" }}>{info}</div>}

        <div style={{ textAlign: "center" }}>
          Već imaš nalog? <Link to="/login">Prijavi se</Link>
        </div>
      </form>
    </div>
  );
};

export default Register;
