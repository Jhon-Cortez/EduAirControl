import { IoArrowBack } from 'react-icons/io5'
import './BackButton.css'

function BackButton({ onClick }) {
  return (
    <button className="back-button" onClick={onClick} aria-label="Volver">
      <IoArrowBack />
    </button>
  )
}

export default BackButton
