{
  /* 기존 이미지 표시 */
}
{
  !petPreviews[pet.petId] && pet.imagePath && (
    <img
      src={`/api/pets/image/${pet.petId}/${
        pet.thumbnailPath
          ? pet.thumbnailPath.split("/").pop()
          : pet.imagePath.split("/").pop()
      }`}
      alt={pet.petName}
      style={{
        width: 120,
        height: 120,
        objectFit: "cover",
        borderRadius: "8px",
        marginTop: "8px",
      }}
    />
  );
}
