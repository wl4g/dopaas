import { store } from "@/utils";

export function getUserName() {
    return store.get("userinfo.username")
}
