/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.medlinker.im.im.core.util;

/**
 * Retry policy for a request.
 * Created by jiantao on 2017/3/8.
 */
public interface RetryPolicy {

    /**
     * Returns the current retry count .
     */
    int getCurrentRetryCount();

    /**
     * Prepares for the next retry.
     * @param ex
     * @throws Exception
     */
    void retry(Exception ex) throws Exception;

    /**
     * reset current retry count
     */
    void reset();
}
