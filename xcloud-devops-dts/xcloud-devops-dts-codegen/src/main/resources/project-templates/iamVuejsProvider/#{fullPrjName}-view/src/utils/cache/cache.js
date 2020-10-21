import {
    gbs
} from 'config/'

class Cache {
    constructor() {
        this.store = window.localStorage
        this.prefix = gbs.db_prefix
    }
    set(key, value, fn) {
        try {
            value = JSON.stringify(value)
        } catch (e) {
        }
        this.store.setItem(this.prefix + key, value)
        fn && fn()
    }
    get(key, fn) {
        if (!key) {
            throw new Error('Cannot get store element, because no such key of ' + key)
        }
        if (typeof key === 'object') {
            throw new Error('Cannot get store element, because the key type is object')
        }
        var value = this.store.getItem(this.prefix + key)
        if (value !== null) {
            try {
                value = JSON.parse(value)
            } catch (e) {
            }
        }
        return value
    }

    remove(key) {
        this.store.removeItem(this.prefix + key)
    }
}

export default new Cache()
