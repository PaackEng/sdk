package api_client

import (
	"sync"
	"time"
)

type Credentials struct {
	Audience string
	ClientId string
}

type Token struct {
	authToken   string
	credentials Credentials
}

type CachedToken struct {
	token             Token
	expireAtTimestamp int64
}

type localCache struct {
	stop chan struct{}

	wg     sync.WaitGroup
	mu     sync.RWMutex
	tokens map[Credentials]CachedToken
}

func newLocalCache(cleanupInterval time.Duration) *localCache {
	lc := &localCache{
		tokens: make(map[Credentials]CachedToken, 0),
		stop:   make(chan struct{}),
	}

	lc.wg.Add(1)
	go func(cleanupInterval time.Duration) {
		defer lc.wg.Done()
		lc.resetLoop(cleanupInterval)
	}(cleanupInterval)

	return lc
}

func (lc *localCache) resetLoop(interval time.Duration) {
	t := time.NewTicker(interval)
	defer t.Stop()

	for {
		select {
		case <-lc.stop:
			return
		case <-t.C:
			lc.mu.Lock()
			for k, v := range lc.tokens {
				if v.expireAtTimestamp <= time.Now().Unix() {
					delete(lc.tokens, k)
				}
			}

			lc.mu.Unlock()
		}
	}
}

func (lc *localCache) stopCleanup() {
	close(lc.stop)
	lc.wg.Wait()
}

func (lc *localCache) update(token Token, expireAtTimestamp int64) {
	lc.mu.Lock()
	defer lc.mu.Unlock()

	lc.tokens[token.credentials] = CachedToken{
		token:             token,
		expireAtTimestamp: expireAtTimestamp,
	}
}

func (lc *localCache) Read(credentials Credentials) *CachedToken {
	lc.mu.RLock()
	defer lc.mu.RUnlock()

	ct, ok := lc.tokens[credentials]
	if !ok {
		return nil
	}

	return &ct
}

func (lc *localCache) delete(credentials Credentials) {
	lc.mu.Lock()
	defer lc.mu.Unlock()

	delete(lc.tokens, credentials)
}
