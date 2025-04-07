import 'leaflet/dist/leaflet.css'; // Correct way to import Leaflet CSS
import L from 'leaflet';

const template = document.createElement('template');
template.innerHTML = `
  <style>
    #map {
      width: 100%;
      height: 100%;
    }
  </style>
  <div id="map"></div>
`;

class MapComponent extends HTMLElement {
  connectedCallback() {
    this.attachShadow({ mode: 'open' });
    this.shadowRoot.appendChild(template.content.cloneNode(true));

    const mapElement = this.shadowRoot.getElementById('map');
    const map = L.map(mapElement).setView([51.505, -0.09], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);
  }
}

customElements.define('map-component', MapComponent);