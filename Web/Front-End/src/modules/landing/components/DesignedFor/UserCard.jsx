import "./DesignedFor.css";

function UserCard({ icon: Icon, title, description }) {
  return (
    <div className="user-card">
      <div className="user-icon">
        <Icon size={32}/>
      </div>

      <h3>{title}</h3>

      <p>{description}</p>
    </div>
  );
}

export default UserCard;