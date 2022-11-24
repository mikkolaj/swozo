// export const ADDR = "http://localhost:8080";
export const ADDR = window.location.origin;

export const getUkey = () => {
  return new URLSearchParams(window.location.search).get("ukey");
};
