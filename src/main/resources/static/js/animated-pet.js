
document.addEventListener("DOMContentLoaded", () => {
    const pet = document.createElement("div");

    pet.id = "animated-pet";

    pet.innerHTML = `
        <div id="pet-message">မင်္ဂလာပါ! 🐾</div>
        <div id="pet-character" title="Click me!">🐱</div>
    `;

    document.body.appendChild(pet);

    const character = document.getElementById("pet-character");
    const message = document.getElementById("pet-message");

    character.addEventListener("click", () => {
        message.style.display = "block";
        message.textContent = "Hello! Welcome to my website! 🐾";

        character.classList.remove("pet-jump");
        void character.offsetWidth;
        character.classList.add("pet-jump");
    });
});
